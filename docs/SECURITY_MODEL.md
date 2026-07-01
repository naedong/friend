# Security Model

The platform is deny-by-default. A booking is rejected unless every required safety condition passes.

Core rules:

- No anonymous booking.
- No unverified companion can receive bookings.
- No unapproved companion profile can receive bookings.
- No unsafe category can be booked.
- No unsafe meeting spot can be booked.
- No private-space meeting is allowed.
- Sensitive actions create audit logs.
- Client-provided role, verification status, booking status, safety status, and future price data must not be trusted.

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

- `AuditLogService` records sensitive actions with hashed IP/user-agent metadata.
- Audit logs are append-only by API design; no update/delete endpoints are implemented.
- Sensitive actions include accept, check-in, check-out, report, block, safety hold, and payout hold transitions.

Development auth shortcut:

- The temporary `X-Dev-Actor-Id` actor provider is limited to dev/test profiles and also requires `friend.security.dev-actor-enabled=true`.
- Production must use real Spring Security principals. If no production authentication provider is configured, the backend should fail closed rather than accept a mock actor.

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
- Add production authentication, authorization policies, rate limiting, abuse detection, secret management, and security headers.
- Add audit-log retention policy and tamper-evidence strategy.
- Add threat modeling for KYC, payments, location, safety-card sharing, and moderator tooling.
