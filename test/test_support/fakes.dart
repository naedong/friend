import 'dart:async';

import 'package:friend/app/friend_app_controller.dart';
import 'package:friend/features/booking/models/booking_category.dart';
import 'package:friend/features/booking/models/booking_options.dart';
import 'package:friend/features/booking/models/booking_request.dart';
import 'package:friend/features/booking/models/booking_response.dart';
import 'package:friend/features/booking/services/booking_api.dart';
import 'package:friend/features/report/models/report_request.dart';
import 'package:friend/features/report/services/report_api.dart';
import 'package:friend/features/safety_card/models/safety_card_view.dart';
import 'package:friend/features/safety_card/services/safety_card_api.dart';

const testCompanion = CompanionOption(
  id: '00000000-0000-0000-0000-000000000002',
  displayName: 'Mina Park',
  bio: 'Verified public-place companion.',
  identityVerified: true,
  livenessVerified: true,
);

const testMeetingSpot = MeetingSpotOption(
  id: '00000000-0000-0000-0000-000000000004',
  name: 'Central Library Cafe',
  address: '1 Public Square',
  type: 'LIBRARY',
);

const testBookingOptions = BookingOptions(
  companions: [testCompanion],
  meetingSpots: [testMeetingSpot],
);

BookingResponse testBooking({
  BookingStatusLabel status = BookingStatusLabel.requested,
}) {
  return BookingResponse(
    id: '00000000-0000-0000-0000-000000000003',
    customerId: '00000000-0000-0000-0000-000000000001',
    companionId: testCompanion.id,
    category: BookingCategory.coffeeBuddy,
    meetingSpotId: testMeetingSpot.id,
    startTime: DateTime.utc(2026, 7, 13, 10),
    endTime: DateTime.utc(2026, 7, 13, 11),
    status: status,
  );
}

final testSafetyCardView = SafetyCardView(
  safetyCardReference: 'SC-PUBLIC-EXAMPLE',
  category: 'COFFEE_BUDDY',
  meetingSpot: const SafetyCardMeetingSpot(
    name: 'Central Library Cafe',
    address: '1 Public Square',
  ),
  startTime: DateTime(2026, 7, 13, 10),
  endTime: DateTime(2026, 7, 13, 11),
  companionDisplayName: 'Mina Park',
  customerDisplayName: 'Alex Morgan',
  verificationSummary: const VerificationSummary(
    companionIdentityVerified: true,
    companionLivenessVerified: true,
  ),
  emergencyInstructions: 'Stay public. Use Report if anything feels unsafe.',
);

class FakeBookingGateway implements BookingGateway {
  FakeBookingGateway({
    this.options = testBookingOptions,
    BookingResponse? booking,
  }) : booking = booking ?? testBooking();

  BookingOptions options;
  BookingResponse booking;
  Object? optionsError;
  Object? createError;
  Object? latestBookingError;
  BookingResponse? latestBooking;
  Completer<BookingResponse>? createCompleter;
  int optionsCalls = 0;
  int createCalls = 0;
  int latestBookingCalls = 0;
  BookingRequest? lastRequest;

  @override
  Future<BookingOptions> getOptions() async {
    optionsCalls += 1;
    if (optionsError case final error?) {
      throw error;
    }
    return options;
  }

  @override
  Future<BookingResponse?> getLatestBooking() async {
    latestBookingCalls += 1;
    if (latestBookingError case final error?) {
      throw error;
    }
    return latestBooking;
  }

  @override
  Future<BookingResponse> createBooking(BookingRequest request) async {
    createCalls += 1;
    lastRequest = request;
    if (createError case final error?) {
      throw error;
    }
    if (createCompleter case final completer?) {
      return completer.future;
    }
    return booking;
  }

  @override
  Future<BookingResponse> acceptBooking(String bookingId) async {
    booking = _withStatus(BookingStatusLabel.accepted);
    return booking;
  }

  @override
  Future<BookingResponse> checkIn(
    String bookingId,
    CheckInOutRequest request,
  ) async {
    booking = _withStatus(BookingStatusLabel.checkedIn);
    return booking;
  }

  @override
  Future<BookingResponse> checkOut(
    String bookingId,
    CheckInOutRequest request,
  ) async {
    booking = _withStatus(BookingStatusLabel.checkoutPending);
    return booking;
  }

  BookingResponse _withStatus(BookingStatusLabel status) {
    return BookingResponse(
      id: booking.id,
      customerId: booking.customerId,
      companionId: booking.companionId,
      category: booking.category,
      meetingSpotId: booking.meetingSpotId,
      startTime: booking.startTime,
      endTime: booking.endTime,
      status: status,
    );
  }
}

class FakeReportGateway implements ReportGateway {
  Object? error;
  int calls = 0;
  ReportRequest? lastRequest;

  @override
  Future<ReportResponse> reportBooking(
    String bookingId,
    ReportRequest request,
  ) async {
    calls += 1;
    lastRequest = request;
    if (error case final reportError?) {
      throw reportError;
    }
    return ReportResponse(
      id: '00000000-0000-0000-0000-000000000005',
      reporterId: '00000000-0000-0000-0000-000000000001',
      reportedUserId: request.reportedUserId,
      bookingId: bookingId,
      reason: request.reason,
      status: 'OPEN',
    );
  }
}

class FakeSafetyCardGateway implements SafetyCardGateway {
  FakeSafetyCardGateway({SafetyCardView? view})
    : view = view ?? testSafetyCardView;

  SafetyCardView view;
  Object? error;
  int calls = 0;

  @override
  Future<SafetyCardView> getSafetyCard(String token) async {
    calls += 1;
    if (error case final loadError?) {
      throw loadError;
    }
    return view;
  }
}

FriendAppController buildTestController({
  FakeBookingGateway? bookingGateway,
  FakeReportGateway? reportGateway,
  BookingResponse? initialBooking,
}) {
  return FriendAppController(
    bookingGateway: bookingGateway ?? FakeBookingGateway(),
    reportGateway: reportGateway ?? FakeReportGateway(),
    currentUserId: '00000000-0000-0000-0000-000000000001',
    now: () => DateTime(2026, 7, 12, 9),
    initialBooking: initialBooking,
  );
}
