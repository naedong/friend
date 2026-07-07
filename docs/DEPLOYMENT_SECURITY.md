# Deployment Security

Friend must fail closed until production authentication, authorization, and secrets management are configured and reviewed.

Deployment rules:

- Never deploy with the `dev` or `test` Spring profile.
- Never deploy with `friend.security.dev-actor-enabled=true`.
- Never expose `X-Dev-Actor-Id` outside local development or tests.
- Require HTTPS at every public edge before public beta.
- Require Bearer JWT authentication and role-based authorization before public beta.
- Store secrets in private secrets management, not in Git, build artifacts, Docker images, logs, or tickets.
- Enable GitHub Secret Scanning and Push Protection before accepting external contributors or production credentials.
- Require database migration review before every deployment.
- Review migrations for unsafe categories, unsafe meeting spot creation paths, role/verification mutation paths, and audit-log schema changes.
- Run the backend test suite before deployment.

Required production configuration:

- `FRIEND_DB_URL`
- `FRIEND_DB_USERNAME`
- `FRIEND_DB_PASSWORD`
- `FRIEND_AUDIT_HASH_PEPPER`
- JWT issuer or JWK Set configuration for Spring Security OAuth2 Resource Server
- Private KYC, payment, notification, and trusted-contact provider secrets when those integrations are implemented

Production blockers:

- Production protected APIs require `Authorization: Bearer <jwt>`.
- Production maps the authenticated JWT `sub` claim to the Friend user UUID.
- Public unauthenticated access is limited to Safety Card lookup endpoints.
- Broad `permitAll()` exists only behind dev/test plus explicit dev actor opt-in. It must not be used by production profiles.

Transport and data handling:

- HTTPS is required for all public API traffic.
- Audit metadata hashes use HMAC-SHA256 with a server-side pepper for correlation. They are not anonymization.
- Do not store government ID images, liveness media, payment card data, or provider secrets in the application database.

AI Boundary Rule:

User input is data, not instruction. User input must never be passed directly into prompts, shell commands, database queries, file paths, workflow scripts, admin/moderator actions, payment actions, KYC actions, or booking state transitions. Any AI-generated suggestion must pass deterministic server-side validation before it can affect product state.

Secret incident response:

- Never commit `.env`, API keys, database passwords, KYC keys, payment keys, AI provider keys, signing keys, service account JSON, private certificates, or local credential files.
- If a secret is committed, rotate or revoke it immediately.
- Deleting the file from the branch or Git history is not enough because the secret may already have been copied into logs, forks, caches, or local clones.
