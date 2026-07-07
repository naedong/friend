# Data Model

Major tables:

- `app_user`: account identity, display name, server-controlled role, lifecycle status.
- `user_verification`: email/phone flags, identity/liveness status, provider name, provider reference id, verified timestamp.
- `companion_profile`: companion approval status, bio, approval timestamp.
- `trusted_contact`: trusted contact name, phone, optional email, active flag.
- `safe_meeting_spot`: public meeting location and safety flags.
- `booking`: customer, companion, allowed category, meeting spot, time range, server-controlled status.
- `booking_checkin`: check-in/check-out record, actor user id, optional location, created timestamp.
- `safety_card`: public token, non-secret public reference, internal booking id, expiration timestamp.
- `booking_safety_event`: safety event type, severity, message.
- `report`: reporter, reported user, booking, reason, review status.
- `user_block`: blocker, blocked user, created timestamp.
- `audit_log`: actor, action, target, reason, hashed request metadata, created timestamp.

Security-sensitive fields:

- `app_user.role` and `app_user.status` are server-controlled.
- `user_verification.identity_status` and `liveness_status` are provider-backed and server-controlled.
- `companion_profile.status` controls whether a companion can receive bookings.
- `safe_meeting_spot` safety flags determine whether a location can be booked.
- `booking.status` is changed only by `BookingStateMachine`.
- `audit_log.ip_hash` and `user_agent_hash` store hashes, not raw network metadata.
- `booking_checkin` has one row per booking, participant, and check-in/check-out type.
- `safety_card.public_reference` is safe to show in public responses; internal booking UUIDs stay server-side.

Data intentionally not stored:

- Government ID images.
- Raw liveness media.
- Payment card data.
- Open chat content.
- Romantic, adult, private-space, or clubbing category data.
