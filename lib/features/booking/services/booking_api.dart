import '../../../core/network/api_client.dart';
import '../models/booking_request.dart';
import '../models/booking_response.dart';

class BookingApi {
  const BookingApi(this._apiClient);

  final ApiClient _apiClient;

  Future<BookingResponse> createBooking(BookingRequest request) async {
    final json = await _apiClient.postJson(
      '/api/bookings',
      body: request.toJson(),
    );
    return BookingResponse.fromJson(json);
  }

  Future<BookingResponse> acceptBooking(String bookingId) async {
    final json = await _apiClient.postJson('/api/bookings/$bookingId/accept');
    return BookingResponse.fromJson(json);
  }

  Future<BookingResponse> checkIn(
    String bookingId,
    CheckInOutRequest request,
  ) async {
    final json = await _apiClient.postJson(
      '/api/bookings/$bookingId/check-in',
      body: request.toJson(),
    );
    return BookingResponse.fromJson(json);
  }

  Future<BookingResponse> checkOut(
    String bookingId,
    CheckInOutRequest request,
  ) async {
    final json = await _apiClient.postJson(
      '/api/bookings/$bookingId/check-out',
      body: request.toJson(),
    );
    return BookingResponse.fromJson(json);
  }
}
