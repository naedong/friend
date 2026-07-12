import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/app/friend_app.dart';
import 'package:friend/core/config/app_environment.dart';
import 'package:friend/core/security/dev_actor_config.dart';
import 'package:friend/features/booking/models/booking_category.dart';

import 'test_support/fakes.dart';

void main() {
  testWidgets('booking flow keeps selections and submits to the gateway', (
    tester,
  ) async {
    final bookingGateway = FakeBookingGateway();
    final controller = buildTestController(bookingGateway: bookingGateway);
    addTearDown(controller.dispose);

    await tester.pumpWidget(
      FriendApp(
        environment: _testEnvironment(),
        controller: controller,
        safetyCardGateway: FakeSafetyCardGateway(),
      ),
    );

    await tester.tap(find.text('I understand'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Start booking'));
    await tester.pumpAndSettle();

    await tester.tap(
      find.byKey(ValueKey('category-${BookingCategory.coffeeBuddy.apiValue}')),
    );
    await tester.pumpAndSettle();

    await tester.tap(find.byKey(ValueKey('companion-${testCompanion.id}')));
    await tester.pumpAndSettle();
    await tester.tap(
      find.byKey(ValueKey('meeting-spot-${testMeetingSpot.id}')),
    );
    await tester.pump();
    await tester.ensureVisible(find.text('Review booking'));
    await tester.tap(find.text('Review booking'));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Request booking'));
    await tester.tap(find.text('Request booking'));
    await tester.pumpAndSettle();

    expect(find.text('Current status'), findsOneWidget);
    expect(find.text('Requested'), findsOneWidget);
    expect(bookingGateway.createCalls, 1);
    expect(controller.selectedCategory, BookingCategory.coffeeBuddy);
    expect(controller.selectedCompanion?.id, testCompanion.id);
    expect(controller.selectedMeetingSpot?.id, testMeetingSpot.id);
  });
}

AppEnvironment _testEnvironment() {
  return AppEnvironment(
    kind: EnvironmentKind.dev,
    apiBaseUrl: Uri.parse('http://localhost:8080'),
    devActorConfig: DevActorConfig(isProduction: false, enabled: false),
  );
}
