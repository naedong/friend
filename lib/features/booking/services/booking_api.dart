import '../../../core/network/api_client.dart';
import '../models/booking_options.dart';
import '../models/booking_request.dart';
import '../models/booking_response.dart';

abstract interface class BookingGateway {
  Future<BookingOptions> getOptions();

  Future<BookingResponse?> getLatestBooking();

  Future<BookingResponse> createBooking(BookingRequest request);

  Future<BookingResponse> acceptBooking(String bookingId);

  Future<BookingResponse> checkIn(String bookingId, CheckInOutRequest request);

  Future<BookingResponse> checkOut(String bookingId, CheckInOutRequest request);
}

class BookingApi implements BookingGateway {
  const BookingApi(this._apiClient);

  final ApiClient _apiClient;

  @override
  Future<BookingOptions> getOptions() async {
    final json = await _apiClient.get('/api/booking-options');
    return BookingOptions.fromJson(json);
  }

  @override
  Future<BookingResponse?> getLatestBooking() async {
    final json = await _apiClient.get('/api/bookings/latest');
    return json.isEmpty ? null : BookingResponse.fromJson(json);
  }

  @override
  Future<BookingResponse> createBooking(BookingRequest request) async {
    final json = await _apiClient.postJson(
      '/api/bookings',
      body: request.toJson(),
    );
    return BookingResponse.fromJson(json);
  }

  @override
  Future<BookingResponse> acceptBooking(String bookingId) async {
    final json = await _apiClient.postJson('/api/bookings/$bookingId/accept');
    return BookingResponse.fromJson(json);
  }

  @override
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

  @override
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
