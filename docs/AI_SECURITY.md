# AI Security

Friend may later include AI-assisted features, but AI is not part of the trusted control plane.

## AI Boundary Rule

User input is data, not instruction. User input must never be passed directly into prompts, shell commands, database queries, file paths, workflow scripts, admin/moderator actions, payment actions, KYC actions, or booking state transitions. Any AI-generated suggestion must pass deterministic server-side validation before it can affect product state.

## Required Rules

- Never concatenate raw user input directly into system prompts.
- Never allow user input to modify system or developer instructions.
- Never allow user input to become shell command text.
- Never allow user input to become raw SQL or JPQL query text.
- Never allow user input to select arbitrary files or paths.
- Never allow AI output to directly trigger safety, booking, payment, KYC, moderator, or admin actions.
- Treat AI output as advisory only unless a deterministic server-side policy validates the action.
- Store user text as data, not instructions.
- Use allowlists for categories, report reasons, booking states, and meeting spot types.
- Add length limits and dangerous-pattern detection before sending user text to AI providers.

## Backend Boundary

The `com.naedong.friend.ai` package provides a defensive pattern for future AI integrations:

- `AiInputSanitizer` trims AI-bound text, enforces length limits, rejects null bytes and unsafe control characters, and flags obvious prompt-injection or destructive command/query phrases.
- `AiInputPolicy` marks privileged actions that require deterministic server-side validation.
- `AiPromptTemplate` keeps system instructions separate from user text and quotes user text as data.

This sanitizer is only one defense layer. It does not make AI safe by itself and must not be used as authorization, moderation, payment, KYC, or booking-state policy.

## Shell, Query, and File Safety

- Do not call `Runtime.getRuntime().exec` with user input.
- Do not construct `ProcessBuilder` commands from user input.
- Do not concatenate user input into native SQL, JPQL, or annotation queries.
- Do not construct file paths from user input without a narrow server-side allowlist.
- Prefer repository methods, parameterized queries, enums, DTO validation, and allowlists.

## Secret Handling

Enable GitHub Secret Scanning and Push Protection in repository settings.

Never commit `.env`, API keys, database passwords, KYC keys, payment keys, AI provider keys, signing keys, service account JSON, private certificates, or local credential files. If a secret is committed, rotate or revoke it immediately; deleting it from Git history is not enough.
