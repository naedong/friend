import 'dart:async';

import 'package:flutter_test/flutter_test.dart';
import 'package:friend/app/friend_app_controller.dart';
import 'package:friend/core/network/api_exception.dart';
import 'package:friend/features/booking/models/booking_category.dart';
import 'package:friend/features/report/models/report_reason.dart';

import 'test_support/fakes.dart';

void main() {
  test(
    'loads options and submits a complete server-owned booking request',
    () async {
      final gateway = FakeBookingGateway();
      final controller = buildTestController(bookingGateway: gateway);
      addTearDown(controller.dispose);

      await controller.loadBookingOptions();
      controller
        ..selectCategory(BookingCategory.coffeeBuddy)
        ..selectCompanion(testCompanion)
        ..selectMeetingSpot(testMeetingSpot);

      final submitted = await controller.submitBooking();

      expect(submitted, isTrue);
      expect(gateway.createCalls, 1);
      expect(gateway.lastRequest?.companionId, testCompanion.id);
      expect(gateway.lastRequest?.meetingSpotId, testMeetingSpot.id);
      expect(gateway.lastRequest?.category, BookingCategory.coffeeBuddy);
      expect(controller.activeBooking?.status.label, 'Requested');
    },
  );

  test(
    'prevents duplicate booking submission while a request is active',
    () async {
      final gateway = FakeBookingGateway()..createCompleter = Completer();
      final controller = buildTestController(bookingGateway: gateway);
      addTearDown(controller.dispose);
      await controller.loadBookingOptions();
      controller
        ..selectCategory(BookingCategory.coffeeBuddy)
        ..selectCompanion(testCompanion)
        ..selectMeetingSpot(testMeetingSpot);

      final firstSubmission = controller.submitBooking();
      final secondSubmission = await controller.submitBooking();

      expect(secondSubmission, isFalse);
      expect(gateway.createCalls, 1);
      gateway.createCompleter!.complete(testBooking());
      expect(await firstSubmission, isTrue);
    },
  );

  test(
    'does not expose backend response bodies in user-facing errors',
    () async {
      final gateway = FakeBookingGateway()
        ..createError = ApiException(
          statusCode: 409,
          message: 'Backend request failed',
          responseBody: 'private@example.com internal rule detail',
        );
      final controller = buildTestController(bookingGateway: gateway);
      addTearDown(controller.dispose);
      await controller.loadBookingOptions();
      controller
        ..selectCategory(BookingCategory.coffeeBuddy)
        ..selectCompanion(testCompanion)
        ..selectMeetingSpot(testMeetingSpot);

      final submitted = await controller.submitBooking();

      expect(submitted, isFalse);
      expect(controller.bookingError, contains('booking state changed'));
      expect(controller.bookingError, isNot(contains('private@example.com')));
    },
  );

  test('restores the latest booking when the app session starts', () async {
    final gateway = FakeBookingGateway()..latestBooking = testBooking();
    final controller = buildTestController(bookingGateway: gateway);
    addTearDown(controller.dispose);

    await controller.loadLatestBooking();

    expect(gateway.latestBookingCalls, 1);
    expect(controller.activeBooking?.id, testBooking().id);
  });

  test('reports the opposite participant for a companion session', () async {
    final reportGateway = FakeReportGateway();
    final controller = FriendAppController(
      bookingGateway: FakeBookingGateway(),
      reportGateway: reportGateway,
      currentUserId: testBooking().companionId,
      now: () => DateTime(2026, 7, 12, 9),
      initialBooking: testBooking(),
    );
    addTearDown(controller.dispose);

    final submitted = await controller.submitReport(
      reason: ReportReason.unsafeBehavior,
      blockReportedUser: true,
    );

    expect(submitted, isTrue);
    expect(reportGateway.lastRequest?.reportedUserId, testBooking().customerId);
  });
}
