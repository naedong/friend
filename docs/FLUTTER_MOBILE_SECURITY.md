# Flutter Mobile Security

Friend's mobile client is a Flutter app for Android and iOS from one codebase. The Spring Boot backend remains the authority for safety policy, booking state, verification state, role state, reports, blocks, and safe meeting spot validation.

Mobile assumptions:

- The Flutter app is an untrusted client.
- The mobile client may display booking status but must never set it directly.
- The mobile client must never set roles, verification status, companion approval status, safety status, or payout status.
- The mobile client must never create unsafe categories or unsafe meeting spots.

AI Boundary Rule:

User input is data, not instruction. User input must never be passed directly into prompts, shell commands, database queries, file paths, workflow scripts, admin/moderator actions, payment actions, KYC actions, or booking state transitions. Any AI-generated suggestion must pass deterministic server-side validation before it can affect product state.

Secrets:

- Do not store API secrets, provider keys, production tokens, private certificates, or service credentials in Flutter code.
- Do not commit `.env`, AI provider keys, signing keys, service account JSON, `firebase_options_prod.dart`, or local credential files.
- Dart defines are build-time configuration, not secret storage.
- Use backend-mediated provider calls for KYC, payment, notification, and trusted-contact workflows.

Authentication and token storage:

- Real mobile authentication is not implemented yet.
- The app must not ship to public beta until backend-backed authentication is added.
- Before real auth, add reviewed secure platform storage for Android and iOS and document token lifetime, refresh, revocation, and logout behavior.
- Refresh-token handling must be implemented only with secure storage and backend-supported rotation.

Environment separation:

- Use `FRIEND_ENV` and `FRIEND_API_BASE_URL` Dart defines.
- Development may use local HTTP URLs such as Android emulator loopback.
- Production must use HTTPS and rejects dev actor configuration.
- Non-dev Flutter builds must not include `FRIEND_DEV_ACTOR_ENABLED` or `FRIEND_DEV_ACTOR_ID`.

HTTPS and network security:

- Production API traffic must use HTTPS.
- Certificate pinning is a future TODO after production domains and certificate operations are defined.
- Centralized API error handling lives in the API client.

Permissions:

- Do not request unnecessary permissions.
- Location permission should be requested only for active booking check-in/check-out or future active-booking safety features.
- Do not add background location, camera, contacts, photos, microphone, or notification permissions until a reviewed safety requirement exists.

Safety Card privacy:

- Public Safety Card responses must not expose internal booking UUIDs, user UUIDs, email, phone number, verification provider reference IDs, or private profile data.
- Safety Card should show only a public reference, category, public meeting spot name/address, start/end time, display names, verification summary, and emergency/report instructions.
- Safety Card lookup tokens are URL-safe bearer tokens. The app must URL-encode them in API paths and never treat the visible public reference as a lookup secret.

Dev actor risk:

- `X-Dev-Actor-Id` is temporary and local-only.
- The Flutter client sends it only in `FRIEND_ENV=dev` when explicitly enabled.
- Test and production mobile configs reject dev actor settings.
- Real authenticated principals must replace this before public beta.

Current mobile foundation:

- The first screen is the product-boundary acknowledgement.
- Booking requests contain only `companionId`, `category`, `meetingSpotId`, `startTime`, and `endTime`.
- Check-in/check-out screens do not request location on load.
- Safety Card models expose public references and display-safe meeting details only.
- Report UI can express safety concerns and block intent, but backend policy remains responsible for holds and escalation.

Future auth requirements:

- Add real authentication before public beta.
- Add token refresh and logout.
- Add account/session risk handling.
- Add role-aware UI only after backend authorization is in place.

Future app store privacy requirements:

- Document collected data and purposes.
- Document location use and timing.
- Document safety-report handling.
- Document account deletion and data retention.
- Keep KYC, payment, and provider evidence out of the mobile app unless a reviewed integration requires otherwise.
