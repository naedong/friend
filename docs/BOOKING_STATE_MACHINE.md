# Booking State Machine

Statuses:

- `REQUESTED`
- `ACCEPTED`
- `REJECTED`
- `CANCELLED`
- `CHECKED_IN`
- `IN_PROGRESS`
- `CHECKOUT_PENDING`
- `COMPLETED`
- `REPORTED`
- `SAFETY_HOLD`
- `PAYOUT_HOLD`

Valid transitions:

- `REQUESTED -> ACCEPTED`
- `REQUESTED -> REJECTED`
- `REQUESTED -> CANCELLED`
- `REQUESTED -> REPORTED`
- `ACCEPTED -> CHECKED_IN`
- `ACCEPTED -> CANCELLED`
- `ACCEPTED -> REPORTED`
- `CHECKED_IN -> IN_PROGRESS`
- `CHECKED_IN -> REPORTED`
- `IN_PROGRESS -> CHECKOUT_PENDING`
- `IN_PROGRESS -> REPORTED`
- `CHECKOUT_PENDING -> COMPLETED`
- `CHECKOUT_PENDING -> REPORTED`
- `REPORTED -> SAFETY_HOLD`
- `SAFETY_HOLD -> PAYOUT_HOLD`

Invalid examples:

- `REQUESTED -> COMPLETED`
- `COMPLETED -> IN_PROGRESS`
- `REJECTED -> ACCEPTED`
- `CANCELLED -> IN_PROGRESS`

Safety hold behavior:

- Reports and missed checkouts move active bookings through `REPORTED` into `SAFETY_HOLD`.
- `SAFETY_HOLD` means moderator review is required before normal completion or payout decisions.

Payout hold behavior:

- `PAYOUT_HOLD` is reachable from `SAFETY_HOLD`.
- Off-platform payment reports and future high-risk payout events can freeze payouts while review is pending.

All transitions are server-side only through `BookingStateMachine`. Clients never set booking status directly.
