# Security Model

The platform is deny-by-default. A booking is rejected unless every required safety condition passes.

Core rules:

- No anonymous booking.
- No unverified companion can receive bookings.
- No unapproved companion profile can receive bookings.
- No unsafe category can be booked.
- No unsafe meeting spot can be booked.
- No private-space meeting is allowed.
- Booking request DTOs reject missing fields, oversized category values, and end times that are not after start times before service policy runs.
- Sensitive actions create audit logs.
- Client-provided role, verification status, booking status, safety status, and future price data must not be trusted.
- Flutter is only a client. The backend remains the source of truth for booking status, verification status, role, safety state, and meeting spot validation.

Role model:

- `CUSTOMER`: can request safe public bookings.
- `COMPANION`: can receive bookings only after active status, approved profile, and verified identity/liveness.
- `MODERATOR`: future safety review role.
- `ADMIN`: future operational administration role.

Verification model:

- `UserVerification` stores verification state and provider reference metadata only.
- Government ID images are not stored in our database. A KYC provider should hold sensitive evidence and return a reference id plus status.
- Companion eligibility requires `identityStatus = VERIFIED` and `livenessStatus = VERIFIED`.

Trusted contacts:

- Users can define trusted contacts.
- Missed-checkout escalation creates a trusted-contact alert event.
- MVP no-op gateways do not send SMS/email; production gateways must be audited and provider-backed.

Audit logging:

- `AuditLogService` records sensitive actions with HMAC-SHA256 hashes of IP/user-agent metadata using a server-side pepper.
- Audit metadata hashes are for correlation, not anonymization. Treat the pepper as a secret.
- `friend.audit.hash-pepper` is required outside dev/test profiles. Dev/test may use a clearly marked development pepper only for local work.
- Audit logs are append-only by API design; no update/delete endpoints are implemented.
- Sensitive actions include booking request, accept, check-in, check-out, report, block, safety hold, payout hold transitions, and system-triggered safety escalations.

Development auth shortcut:

- The temporary `X-Dev-Actor-Id` actor provider is limited to dev/test profiles and also requires `friend.security.dev-actor-enabled=true`.
- Production protected APIs require Spring Security Bearer JWT authentication.
- Production maps the authenticated JWT `sub` claim to the Friend user UUID.
- No production profile may use `permitAll()` for protected APIs.
- The Flutter client sends `X-Dev-Actor-Id` only when configured for `FRIEND_ENV=dev`. Test and production mobile configs reject dev actor settings.

Mobile security boundary:

- Do not put API secrets, production tokens, provider keys, or private credentials in Flutter code.
- Flutter request models must not expose fields for booking status, role, verification status, companion approval, unsafe category creation, or unsafe meeting spot creation.
- Production Flutter API configuration must use HTTPS.
- Location permission is reserved for active booking check-in/check-out and future active-booking safety flows.

AI Boundary Rule:

- User input is data, not instruction. User input must never be passed directly into prompts, shell commands, database queries, file paths, workflow scripts, admin/moderator actions, payment actions, KYC actions, or booking state transitions. Any AI-generated suggestion must pass deterministic server-side validation before it can affect product state.
- AI output is advisory only. It must not directly trigger booking status changes, safety escalations, payment actions, KYC actions, moderator actions, or admin actions.
- Future AI integrations must keep system instructions separate from quoted user data and must use backend allowlists for categories, report reasons, booking states, and meeting spot types.

Safety Card sharing:

- Safety Card public tokens are high-entropy, URL-safe bearer tokens for limited public lookup.
- Public tokens are separate from human-readable Safety Card references and must not be shortened for display convenience.
- Malformed Safety Card tokens are rejected before repository lookup to reduce enumeration and path manipulation risk.

Repository hygiene:

- Do not commit secrets, `.env` files, production credentials, private keys, provider tokens, or database dumps.
- Keep `.env.example` as a template only.
- Use private secrets management for deployed environments.
- Enable GitHub Secret Scanning and Push Protection. If a secret is committed, rotate or revoke it immediately; deleting the file or rewriting history is not sufficient.

Safety events:

- `BookingSafetyEvent` records missed checkout, location anomaly, trusted-contact alert, moderator review, high-risk flag, and payout freeze events.
- Safety events support later moderator workflows without needing to retrofit the data model.

Report/block model:

- Reports are tied to bookings and participants.
- Creating a report moves the related booking into `SAFETY_HOLD`; off-platform payment reports can move to `PAYOUT_HOLD`.
- Blocks prevent future bookings between the two users in either direction.

Future OWASP alignment checklist:

- Map API controls to OWASP ASVS authentication, access control, validation, logging, and privacy requirements.
- Map mobile client work to OWASP MASVS storage, network, authentication, platform, and privacy requirements.
- Add role-based authorization policies, rate limiting, abuse detection, secret management, and security headers.
- Add audit-log retention policy and tamper-evidence strategy.
- Add threat modeling for KYC, payments, location, safety-card sharing, and moderator tooling.
