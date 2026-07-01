create table app_user (
    id uuid primary key,
    email varchar(255) not null unique,
    phone_number varchar(64) not null unique,
    display_name varchar(255) not null,
    role varchar(32) not null check (role in ('CUSTOMER', 'COMPANION', 'MODERATOR', 'ADMIN')),
    status varchar(32) not null check (status in ('ACTIVE', 'SUSPENDED', 'DELETED')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table user_verification (
    id uuid primary key,
    user_id uuid not null unique references app_user(id),
    email_verified boolean not null,
    phone_verified boolean not null,
    identity_status varchar(32) not null check (identity_status in ('NOT_STARTED', 'PENDING', 'VERIFIED', 'REJECTED', 'EXPIRED')),
    liveness_status varchar(32) not null check (liveness_status in ('NOT_STARTED', 'PENDING', 'VERIFIED', 'REJECTED', 'EXPIRED')),
    provider_name varchar(255),
    provider_reference_id varchar(255),
    verified_at timestamp with time zone,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table companion_profile (
    id uuid primary key,
    user_id uuid not null unique references app_user(id),
    status varchar(32) not null check (status in ('DRAFT', 'PENDING_REVIEW', 'APPROVED', 'REJECTED', 'SUSPENDED')),
    bio varchar(2000),
    approved_at timestamp with time zone,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table trusted_contact (
    id uuid primary key,
    user_id uuid not null references app_user(id),
    name varchar(255) not null,
    phone_number varchar(64) not null,
    email varchar(255),
    active boolean not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table safe_meeting_spot (
    id uuid primary key,
    name varchar(255) not null,
    address varchar(255) not null,
    latitude numeric(9, 6) not null,
    longitude numeric(9, 6) not null,
    type varchar(64) not null check (type in ('CAFE', 'MUSEUM', 'LIBRARY', 'SHOPPING_MALL', 'STATION_PUBLIC_AREA', 'ADMIN_OFFICE', 'HOSPITAL_PUBLIC_AREA')),
    staff_present boolean not null,
    well_lit boolean not null,
    public_entrance boolean not null,
    easy_exit boolean not null,
    alcohol_centered boolean not null,
    private_space boolean not null,
    active boolean not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table booking (
    id uuid primary key,
    customer_id uuid not null references app_user(id),
    companion_id uuid not null references app_user(id),
    category varchar(64) not null check (category in ('COFFEE_BUDDY', 'MUSEUM_EXHIBITION_BUDDY', 'CITY_WALK_BUDDY', 'LANGUAGE_PRACTICE_BUDDY', 'SAFE_TRADE_BUDDY', 'ADMIN_APPOINTMENT_BUDDY')),
    meeting_spot_id uuid not null references safe_meeting_spot(id),
    start_time timestamp with time zone not null,
    end_time timestamp with time zone not null,
    status varchar(32) not null check (status in ('REQUESTED', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'CHECKED_IN', 'IN_PROGRESS', 'CHECKOUT_PENDING', 'COMPLETED', 'REPORTED', 'SAFETY_HOLD', 'PAYOUT_HOLD')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint booking_distinct_users check (customer_id <> companion_id),
    constraint booking_valid_time_range check (end_time > start_time)
);

create table booking_checkin (
    id uuid primary key,
    booking_id uuid not null references booking(id),
    user_id uuid not null references app_user(id),
    type varchar(32) not null check (type in ('CHECK_IN', 'CHECK_OUT')),
    latitude numeric(9, 6),
    longitude numeric(9, 6),
    created_at timestamp with time zone not null
);

create table safety_card (
    id uuid primary key,
    booking_id uuid not null unique references booking(id),
    public_token varchar(255) not null unique,
    expires_at timestamp with time zone not null,
    created_at timestamp with time zone not null
);

create table booking_safety_event (
    id uuid primary key,
    booking_id uuid not null references booking(id),
    type varchar(64) not null check (type in ('CHECKOUT_MISSED', 'LOCATION_ANOMALY', 'TRUSTED_CONTACT_ALERTED', 'MODERATOR_REVIEW_CREATED', 'HIGH_RISK_FLAGGED', 'PAYOUT_FROZEN')),
    severity varchar(32) not null check (severity in ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    message varchar(1000) not null,
    created_at timestamp with time zone not null
);

create table report (
    id uuid primary key,
    reporter_id uuid not null references app_user(id),
    reported_user_id uuid not null references app_user(id),
    booking_id uuid not null references booking(id),
    reason varchar(64) not null check (reason in ('HARASSMENT', 'UNSAFE_BEHAVIOR', 'OFF_PLATFORM_PAYMENT', 'PRIVATE_MEETING_REQUEST', 'SEXUAL_OR_SUGGESTIVE_BEHAVIOR', 'FAKE_IDENTITY', 'NO_SHOW', 'OTHER')),
    status varchar(32) not null check (status in ('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'DISMISSED')),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    constraint report_distinct_users check (reporter_id <> reported_user_id)
);

create table user_block (
    id uuid primary key,
    blocker_id uuid not null references app_user(id),
    blocked_user_id uuid not null references app_user(id),
    created_at timestamp with time zone not null,
    constraint user_block_distinct_users check (blocker_id <> blocked_user_id),
    constraint user_block_unique_pair unique (blocker_id, blocked_user_id)
);

create table audit_log (
    id uuid primary key,
    actor_user_id uuid references app_user(id),
    action varchar(255) not null,
    target_type varchar(255) not null,
    target_id uuid not null,
    reason varchar(1000),
    ip_hash varchar(64),
    user_agent_hash varchar(64),
    created_at timestamp with time zone not null
);

create index idx_booking_customer_id on booking(customer_id);
create index idx_booking_companion_id on booking(companion_id);
create index idx_booking_status_end_time on booking(status, end_time);
create index idx_booking_safety_event_booking_id on booking_safety_event(booking_id);
create index idx_report_booking_id on report(booking_id);
create index idx_report_reporter_booking on report(reporter_id, booking_id);
create index idx_audit_log_target on audit_log(target_type, target_id);
