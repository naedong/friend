alter table safety_card
    add column public_reference varchar(64);

create unique index ux_safety_card_public_reference
    on safety_card(public_reference)
    where public_reference is not null;

create unique index ux_booking_checkin_booking_user_type
    on booking_checkin(booking_id, user_id, type);
