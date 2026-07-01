# Product Boundary

Friend -- Safe Companion Service is a safety-first public companion booking platform.

It is not:

- a dating app
- an adult service
- a boyfriend/girlfriend rental app
- a clubbing companion app
- a sexual, suggestive, romantic, or physical-touch service

The MVP allows only public, purpose-based, low-risk categories:

- `COFFEE_BUDDY`
- `MUSEUM_EXHIBITION_BUDDY`
- `CITY_WALK_BUDDY`
- `LANGUAGE_PRACTICE_BUDDY`
- `SAFE_TRADE_BUDDY`
- `ADMIN_APPOINTMENT_BUDDY`

Blocked categories are not represented in the backend enum and cannot be created through the normal API:

- `ROMANTIC_DATE`
- `BOYFRIEND_GIRLFRIEND_RENTAL`
- `CLUBBING_COMPANION`
- `DRINKING_BUDDY`
- `HOTEL_MEETING`
- `HOME_VISIT`
- `CAR_MEETING`
- `PHYSICAL_TOUCH_SERVICE`
- `SEXUAL_ROLEPLAY`
- `EMOTIONAL_DEPENDENCY_SERVICE`

MVP bookings must use approved public meeting spots. Romantic, sexual, alcohol-heavy, private-space, clubbing, home, hotel, car, isolated-place, and physical-touch use cases are outside the product boundary.
