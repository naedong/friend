import 'booking_category.dart';

class BookingRequest {
  const BookingRequest({
    required this.companionId,
    required this.category,
    required this.meetingSpotId,
    required this.startTime,
    required this.endTime,
  });

  final String companionId;
  final BookingCategory category;
  final String meetingSpotId;
  final DateTime startTime;
  final DateTime endTime;

  Map<String, Object?> toJson() {
    return {
      'companionId': companionId,
      'category': category.apiValue,
      'meetingSpotId': meetingSpotId,
      'startTime': startTime.toUtc().toIso8601String(),
      'endTime': endTime.toUtc().toIso8601String(),
    };
  }
}

class CheckInOutRequest {
  const CheckInOutRequest({this.latitude, this.longitude});

  final double? latitude;
  final double? longitude;

  Map<String, Object?> toJson() {
    return {
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
    };
  }
}
