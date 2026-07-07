import '../../../core/config/allowed_categories.dart';

enum BookingCategory {
  coffeeBuddy(
    'COFFEE_BUDDY',
    'Coffee Buddy',
    'A short public cafe booking in a staffed, easy-to-leave location.',
  ),
  museumExhibitionBuddy(
    'MUSEUM_EXHIBITION_BUDDY',
    'Museum / Exhibition Buddy',
    'A public cultural visit with clear start and end times.',
  ),
  cityWalkBuddy(
    'CITY_WALK_BUDDY',
    'City Walk Buddy',
    'A daylight-oriented walk through public, accessible areas.',
  ),
  languagePracticeBuddy(
    'LANGUAGE_PRACTICE_BUDDY',
    'Language Practice Buddy',
    'A public language practice session in an approved meeting spot.',
  ),
  safeTradeBuddy(
    'SAFE_TRADE_BUDDY',
    'Safe Trade Buddy',
    'Support for public second-hand trade handoffs at safe locations.',
  ),
  adminAppointmentBuddy(
    'ADMIN_APPOINTMENT_BUDDY',
    'Admin / Appointment Buddy',
    'Public appointment support for offices, hospitals, or admin buildings.',
  );

  const BookingCategory(this.apiValue, this.label, this.safetyDescription);

  final String apiValue;
  final String label;
  final String safetyDescription;
}

const allowedBookingCategories = BookingCategory.values;

bool bookingCategoriesMatchBackendAllowList() {
  final mobileValues = allowedBookingCategories
      .map((category) => category.apiValue)
      .toList();
  return mobileValues.length == allowedCategoryApiValues.length &&
      mobileValues.every(allowedCategoryApiValues.contains);
}
