# Friend -- Safe Companion Service

Friend is a safety-first public companion booking platform. It is not a dating app, not an adult service, and not a boyfriend/girlfriend rental app.

This repository currently contains a default Flutter scaffold plus the initial Spring Boot backend foundation under `backend/`. Mobile UI work is intentionally not implemented yet.

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

## Run Tests

```powershell
cd backend
.\gradlew.bat test
```

The backend is configured for Java 21. If the local machine does not have Java 21, Gradle toolchain auto-download is enabled.

## Documentation

- `docs/PRODUCT_BOUNDARY.md`
- `docs/SECURITY_MODEL.md`
- `docs/BOOKING_STATE_MACHINE.md`
- `docs/SAFETY_ESCALATION.md`
- `docs/DATA_MODEL.md`

## Current Security TODOs

- Replace dev actor header with real authenticated principals.
- Add role-based authorization and moderator/admin policy checks.
- Add production KYC, notification, trusted-contact alert, and payment provider integrations.
- Add rate limiting, abuse detection, and duplicate-report hardening.
- Add integration tests with PostgreSQL/Testcontainers.
- Add operational controls for audit-log retention and tamper evidence.

This is a security-first foundation, not a complete production app.
