# Friend -- Safe Companion Service

Friend is a safety-first public companion booking platform. It is not a dating app, not an adult service, and not a boyfriend/girlfriend rental app.

Repository layout:

- Flutter root app: Android/iOS mobile client from one Flutter codebase.
- `backend/`: Spring Boot security-first backend and source of truth for safety rules.

## What Is Implemented

- Spring Boot 3 backend skeleton with Java 21 toolchain configuration.
- PostgreSQL persistence with Flyway migration.
- Domain model for users, verification, companion profiles, trusted contacts, safe meeting spots, bookings, check-ins, reports, blocks, safety cards, safety events, and audit logs.
- Booking policy gates for verification, approved companion profiles, allowed categories, safe meeting spots, blocked relationships, self-booking, and max duration.
- Booking state machine with audited server-side transitions.
- Minimal internal REST endpoints for booking, accept, check-in, check-out, report, and safety-card lookup.
- Development-only actor header placeholder: `X-Dev-Actor-Id`.
- No-op local gateways for notifications, trusted-contact alerts, KYC, and payments.
- Focused service tests for the core safety requirements.
- Flutter mobile structure under `lib/app`, `lib/core`, and `lib/features`.
- Flutter API request models that do not carry booking status, role, verification status, or companion approval fields.
- Safe MVP mobile skeleton screens for product boundary, booking, check-in/out, Safety Card, report, and profile.

## Repository Hygiene

Do not commit secrets. This repository must not contain production credentials, API keys, private certificates, real provider tokens, `.env` files, or database dumps. Use `.env.example` only as a local configuration template.

Enable GitHub Secret Scanning and Push Protection for this repository. If a secret is committed, rotate or revoke it immediately; do not only delete it from Git history. Never commit `.env`, API keys, database passwords, KYC keys, payment keys, AI provider keys, signing keys, service account JSON, private certificates, or local credential files.

## Security Review Snapshot

Last reviewed: 2026-07-07.

Reviewed areas:

- Login/auth placeholder: real production authentication is still not implemented. The temporary `X-Dev-Actor-Id` shortcut is restricted to `dev`/`test` profiles and requires explicit `friend.security.dev-actor-enabled=true`.
- Authorization boundary: clients do not submit role, verification status, booking status, safety state, or approval state. The backend remains the source of truth for booking state transitions and safety policy.
- API keys and secrets: `.gitignore` excludes `.env`, private keys, keystores, service-account JSON, credential folders, production Firebase options, and backend build artifacts. A repository scan for common API key/private key patterns found no committed production secret values.
- Privacy logging: backend source uses only fixed operational warning logs for dev actor and development audit pepper. Request metadata for audit logs is stored as HMAC-SHA256 hashes, not raw IP address or raw User-Agent values.
- Public Safety Card privacy: lookup tokens are validated before repository access, and public views avoid internal user IDs, emails, phone numbers, provider references, private profile data, and audit details.

Verification commands used for this snapshot:

```powershell
rg -n "(AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|ghp_[0-9A-Za-z_]{36,}|github_pat_[0-9A-Za-z_]+|sk-[A-Za-z0-9_-]{20,}|-----BEGIN (RSA |EC |OPENSSH |PRIVATE )?PRIVATE KEY-----)" -S --glob "!backend/build/**" --glob "!.git/**"
cd backend
.\gradlew.bat test
```

Production readiness gate: do not deploy publicly until real authentication, role-based authorization, provider-backed secret management, rate limiting, abuse detection, and audit-log retention controls are implemented.

## AI Boundary Rule

User input is data, not instruction. User input must never be passed directly into prompts, shell commands, database queries, file paths, workflow scripts, admin/moderator actions, payment actions, KYC actions, or booking state transitions. Any AI-generated suggestion must pass deterministic server-side validation before it can affect product state.

## Run Locally

Start PostgreSQL:

```powershell
docker compose up -d postgres
```

Run the backend:

```powershell
cd backend
.\gradlew.bat bootRun --args='--spring.profiles.active=dev --friend.security.dev-actor-enabled=true'
```

For development-only API calls, include:

```text
X-Dev-Actor-Id: <user-uuid>
```

Real authentication is not implemented yet. The development actor header is isolated behind a dev/test profile component and an explicit `friend.security.dev-actor-enabled=true` property. It must not be used as production auth. The app does not enable the dev shortcut by default; local development must opt in explicitly.

Never deploy the `dev` profile, never enable the dev actor in production, and do not expose this service publicly until real authentication and authorization are implemented. See `docs/DEPLOYMENT_SECURITY.md`.

Run the Flutter app locally:

```powershell
flutter run --dart-define=FRIEND_ENV=dev --dart-define=FRIEND_API_BASE_URL=http://10.0.2.2:8080
```

For local backend dev actor testing only:

```powershell
flutter run --dart-define=FRIEND_ENV=dev --dart-define=FRIEND_DEV_ACTOR_ENABLED=true --dart-define=FRIEND_DEV_ACTOR_ID=<user-uuid>
```

Non-dev Flutter builds must not include `FRIEND_DEV_ACTOR_ENABLED` or `FRIEND_DEV_ACTOR_ID`. Production Flutter builds must use HTTPS API URLs.

## Run Tests

```powershell
cd backend
.\gradlew.bat test
```

Flutter tests:

```powershell
flutter test
```

The backend is configured for Java 21. If the local machine does not have Java 21, Gradle toolchain auto-download is enabled.

## Documentation

- `docs/PRODUCT_BOUNDARY.md`
- `docs/SECURITY_MODEL.md`
- `docs/BOOKING_STATE_MACHINE.md`
- `docs/SAFETY_ESCALATION.md`
- `docs/DATA_MODEL.md`
- `docs/FLUTTER_MOBILE_SECURITY.md`
- `docs/DEPLOYMENT_SECURITY.md`
- `docs/AI_SECURITY.md`

## Current Security TODOs

- Replace dev actor header with real authenticated principals.
- Replace Flutter auth placeholder with real mobile auth and reviewed secure token storage.
- Add role-based authorization and moderator/admin policy checks.
- Add production KYC, notification, trusted-contact alert, and payment provider integrations.
- Add rate limiting, abuse detection, and duplicate-report hardening.
- Add integration tests with PostgreSQL/Testcontainers.
- Add operational controls for audit-log retention and tamper evidence.

This is a security-first foundation, not a complete production app.
