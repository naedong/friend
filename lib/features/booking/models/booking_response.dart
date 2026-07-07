import 'booking_category.dart';

class BookingResponse {
  const BookingResponse({
    required this.id,
    required this.customerId,
    required this.companionId,
    required this.category,
    required this.meetingSpotId,
    required this.startTime,
    required this.endTime,
    required this.status,
  });

  final String id;
  final String customerId;
  final String companionId;
  final BookingCategory category;
  final String meetingSpotId;
  final DateTime startTime;
  final DateTime endTime;
  final BookingStatusLabel status;

  factory BookingResponse.fromJson(Map<String, Object?> json) {
    return BookingResponse(
      id: json['id'] as String,
      customerId: json['customerId'] as String,
      companionId: json['companionId'] as String,
      category: BookingCategory.values.firstWhere(
        (category) => category.apiValue == json['category'],
      ),
      meetingSpotId: json['meetingSpotId'] as String,
      startTime: DateTime.parse(json['startTime'] as String),
      endTime: DateTime.parse(json['endTime'] as String),
      status: BookingStatusLabel.fromApiValue(json['status'] as String),
    );
  }
}

enum BookingStatusLabel {
  requested('REQUESTED', 'Requested'),
  accepted('ACCEPTED', 'Accepted'),
  checkedIn('CHECKED_IN', 'Checked in'),
  inProgress('IN_PROGRESS', 'In progress'),
  checkoutPending('CHECKOUT_PENDING', 'Checkout pending'),
  completed('COMPLETED', 'Completed'),
  reported('REPORTED', 'Reported'),
  safetyHold('SAFETY_HOLD', 'Safety hold'),
  payoutHold('PAYOUT_HOLD', 'Payout hold');

  const BookingStatusLabel(this.apiValue, this.label);

  final String apiValue;
  final String label;

  static BookingStatusLabel fromApiValue(String value) {
    return BookingStatusLabel.values.firstWhere(
      (status) => status.apiValue == value,
    );
  }
}
