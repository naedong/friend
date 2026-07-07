import 'report_reason.dart';

class ReportRequest {
  const ReportRequest({
    required this.reportedUserId,
    required this.reason,
    required this.blockReportedUser,
  });

  final String reportedUserId;
  final ReportReason reason;
  final bool blockReportedUser;

  Map<String, Object?> toJson() {
    return {
      'reportedUserId': reportedUserId,
      'reason': reason.apiValue,
      'blockReportedUser': blockReportedUser,
    };
  }
}

class ReportResponse {
  const ReportResponse({
    required this.id,
    required this.reporterId,
    required this.reportedUserId,
    required this.bookingId,
    required this.reason,
    required this.status,
  });

  final String id;
  final String reporterId;
  final String reportedUserId;
  final String bookingId;
  final ReportReason reason;
  final String status;

  factory ReportResponse.fromJson(Map<String, Object?> json) {
    return ReportResponse(
      id: json['id'] as String,
      reporterId: json['reporterId'] as String,
      reportedUserId: json['reportedUserId'] as String,
      bookingId: json['bookingId'] as String,
      reason: ReportReason.values.firstWhere(
        (reason) => reason.apiValue == json['reason'],
      ),
      status: json['status'] as String,
    );
  }
}
