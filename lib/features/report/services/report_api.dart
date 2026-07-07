import '../../../core/network/api_client.dart';
import '../models/report_request.dart';

class ReportApi {
  const ReportApi(this._apiClient);

  final ApiClient _apiClient;

  Future<ReportResponse> reportBooking(
    String bookingId,
    ReportRequest request,
  ) async {
    final json = await _apiClient.postJson(
      '/api/bookings/$bookingId/report',
      body: request.toJson(),
    );
    return ReportResponse.fromJson(json);
  }
}
