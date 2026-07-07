# Safety Escalation

Checkout missed flow:

1. Detect bookings in `IN_PROGRESS` or `CHECKOUT_PENDING` after `endTime`.
2. Create `CHECKOUT_MISSED` safety event.
3. Move booking toward `SAFETY_HOLD` through valid state-machine transitions.
4. Record system-triggered audit logs with `SYSTEM_MISSED_CHECKOUT_ESCALATION` in the reason.

Trusted contact alert flow:

1. Create `TRUSTED_CONTACT_ALERTED` safety event.
2. Call `TrustedContactAlertGateway`.
3. MVP uses a no-op gateway; production must send provider-backed SMS/email and audit provider outcomes.

Moderator review flow:

1. Create `MODERATOR_REVIEW_CREATED` safety event.
2. Call `NotificationGateway`.
3. Future moderator tooling should review booking context, reports, check-in data, and safety-card data.

Payout freeze concept:

- Off-platform payment reports create a `PAYOUT_FROZEN` event.
- `PaymentGateway.freezePayout` is an interface only in the MVP.
- Production payment integration must freeze funds server-side and record provider references.

Limitations:

- The MVP does not integrate with emergency services.
- Users must contact local emergency services directly if there is immediate danger.
- The MVP does not send real SMS, email, push notifications, KYC requests, or payment actions.
