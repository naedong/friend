import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/app/friend_app.dart';
import 'package:friend/core/config/allowed_categories.dart';
import 'package:friend/core/config/app_environment.dart';
import 'package:friend/core/security/dev_actor_config.dart';
import 'package:friend/features/booking/models/booking_category.dart';
import 'package:friend/features/booking/models/booking_request.dart';
import 'package:friend/features/booking/screens/category_selection_screen.dart';
import 'package:friend/features/home/home_screen.dart';
import 'package:friend/features/onboarding/product_boundary_screen.dart';
import 'package:friend/features/safety_card/models/safety_card_view.dart';
import 'package:friend/features/report/models/report_reason.dart';
import 'package:friend/features/report/screens/report_screen.dart';

void main() {
  testWidgets('Product boundary screen says not dating and not adult service', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: ProductBoundaryScreen()));

    expect(
      find.text('Friend is for safe public companion bookings.'),
      findsOneWidget,
    );
    expect(find.text('Not dating.'), findsOneWidget);
    expect(find.text('Not adult service.'), findsOneWidget);
    expect(find.text('Public places only.'), findsOneWidget);
    expect(find.text('I understand'), findsOneWidget);
  });

  test('category list contains only allowed MVP categories', () {
    expect(bookingCategoriesMatchBackendAllowList(), isTrue);
    expect(
      allowedBookingCategories.map((category) => category.apiValue),
      orderedEquals(allowedCategoryApiValues),
    );
  });

  testWidgets('blocked category labels do not appear in category selection', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: CategorySelectionScreen()));

    final visibleText = _visibleText(tester).toLowerCase();
    for (final blockedLabel in blockedCategoryLabels) {
      expect(
        visibleText,
        isNot(contains(blockedLabel)),
        reason: 'Category UI must not show blocked label: $blockedLabel',
      );
    }
  });

  test('BookingRequest JSON does not include server-owned fields', () {
    final request = BookingRequest(
      companionId: 'companion-id',
      category: BookingCategory.coffeeBuddy,
      meetingSpotId: 'meeting-spot-id',
      startTime: DateTime.utc(2026, 7, 2, 10),
      endTime: DateTime.utc(2026, 7, 2, 11),
    );

    final json = request.toJson();

    expect(
      json.keys,
      unorderedEquals([
        'companionId',
        'category',
        'meetingSpotId',
        'startTime',
        'endTime',
      ]),
    );
    expect(json.keys, isNot(contains('customerId')));
    expect(json.keys, isNot(contains('status')));
    expect(json.keys, isNot(contains('role')));
    expect(json.keys, isNot(contains('verificationStatus')));
    expect(json.keys, isNot(contains('safetyState')));
  });

  test('SafetyCardView model does not contain private fields', () {
    const userId = '00000000-0000-0000-0000-000000000001';
    final view = SafetyCardView(
      safetyCardReference: 'SC-PUBLIC-EXAMPLE',
      category: 'COFFEE_BUDDY',
      meetingSpot: const SafetyCardMeetingSpot(
        name: 'Central Library Cafe',
        address: '1 Public Square',
      ),
      startTime: DateTime.utc(2026, 7, 2, 10),
      endTime: DateTime.utc(2026, 7, 2, 11),
      companionDisplayName: 'Companion One',
      customerDisplayName: 'Customer One',
      verificationSummary: const VerificationSummary(
        companionIdentityVerified: true,
        companionLivenessVerified: true,
      ),
      emergencyInstructions: 'Stay public and report unsafe behavior.',
    );

    final encoded = jsonEncode(view.toDisplayJson());

    expect(encoded, contains('SC-PUBLIC-EXAMPLE'));
    expect(encoded, isNot(contains(userId)));
    expect(encoded, isNot(contains('email')));
    expect(encoded, isNot(contains('phone')));
    expect(encoded, isNot(contains('userId')));
    expect(encoded, isNot(contains('providerReferenceId')));
    expect(encoded, isNot(contains('audit')));
  });

  test('production environment does not send X-Dev-Actor-Id', () {
    final environment = AppEnvironment(
      kind: EnvironmentKind.production,
      apiBaseUrl: Uri.parse('https://api.friend.example'),
      devActorConfig: DevActorConfig(isProduction: true, enabled: false),
    );

    expect(environment.requestHeaders(), isNot(contains('X-Dev-Actor-Id')));
  });

  test('non-dev environment rejects X-Dev-Actor-Id configuration', () {
    expect(
      () => AppEnvironment(
        kind: EnvironmentKind.test,
        apiBaseUrl: Uri.parse('http://localhost:8080'),
        devActorConfig: DevActorConfig(
          isProduction: false,
          enabled: true,
          actorId: 'dev-user-id',
        ),
      ),
      throwsStateError,
    );
  });

  testWidgets('Home screen does not contain unsafe entry labels', (
    tester,
  ) async {
    final environment = AppEnvironment(
      kind: EnvironmentKind.dev,
      apiBaseUrl: Uri.parse('http://localhost:8080'),
      devActorConfig: DevActorConfig(isProduction: false, enabled: false),
    );

    await tester.pumpWidget(
      MaterialApp(home: HomeScreen(environment: environment)),
    );

    final visibleText = _visibleText(tester).toLowerCase();
    for (final blockedLabel in [
      'dating',
      'romance',
      'clubbing',
      'nightlife',
      'adult-service',
      'adult service',
    ]) {
      expect(
        visibleText,
        isNot(contains(blockedLabel)),
        reason: 'Home UI must not show blocked label: $blockedLabel',
      );
    }
  });

  testWidgets('Report screen exposes selectable reasons and block toggle', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: ReportScreen()));

    RadioGroup<ReportReason> reasonGroup() =>
        tester.widget<RadioGroup<ReportReason>>(
          find.byType(RadioGroup<ReportReason>),
        );

    expect(reasonGroup().groupValue, ReportReason.unsafeBehavior);

    await tester.ensureVisible(
      find.byKey(
        ValueKey('report-reason-${ReportReason.fakeIdentity.apiValue}'),
      ),
    );

    await tester.tap(
      find.byKey(
        ValueKey('report-reason-${ReportReason.fakeIdentity.apiValue}'),
      ),
    );
    await tester.pump();

    expect(reasonGroup().groupValue, ReportReason.fakeIdentity);

    CheckboxListTile blockTile() => tester.widget<CheckboxListTile>(
      find.byKey(const ValueKey('report-block-user')),
    );

    await tester.ensureVisible(find.byKey(const ValueKey('report-block-user')));
    expect(blockTile().value, isFalse);
    await tester.tap(find.byKey(const ValueKey('report-block-user')));
    await tester.pump();
    expect(blockTile().value, isTrue);
  });

  testWidgets('Friend app starts at product boundary', (tester) async {
    final environment = AppEnvironment(
      kind: EnvironmentKind.dev,
      apiBaseUrl: Uri.parse('http://localhost:8080'),
      devActorConfig: DevActorConfig(isProduction: false, enabled: false),
    );

    await tester.pumpWidget(FriendApp(environment: environment));

    expect(
      find.text('Friend is for safe public companion bookings.'),
      findsOneWidget,
    );
    expect(find.text('I understand'), findsOneWidget);
  });

  test('Android release manifest allows network but rejects cleartext', () {
    final manifest = File(
      'android/app/src/main/AndroidManifest.xml',
    ).readAsStringSync();

    expect(manifest, contains('android.permission.INTERNET'));
    expect(manifest, contains('android:usesCleartextTraffic="false"'));
  });

  test(
    'Mobile platform manifests do not request unreviewed sensitive permissions',
    () {
      final androidManifest = File(
        'android/app/src/main/AndroidManifest.xml',
      ).readAsStringSync();
      final iosInfoPlist = File('ios/Runner/Info.plist').readAsStringSync();

      for (final permission in [
        'ACCESS_FINE_LOCATION',
        'ACCESS_COARSE_LOCATION',
        'ACCESS_BACKGROUND_LOCATION',
        'CAMERA',
        'READ_CONTACTS',
        'RECORD_AUDIO',
        'READ_MEDIA_IMAGES',
        'READ_EXTERNAL_STORAGE',
        'POST_NOTIFICATIONS',
      ]) {
        expect(androidManifest, isNot(contains(permission)));
      }

      for (final privacyKey in [
        'NSLocationWhenInUseUsageDescription',
        'NSLocationAlwaysAndWhenInUseUsageDescription',
        'NSCameraUsageDescription',
        'NSContactsUsageDescription',
        'NSMicrophoneUsageDescription',
        'NSPhotoLibraryUsageDescription',
      ]) {
        expect(iosInfoPlist, isNot(contains(privacyKey)));
      }
    },
  );
}

String _visibleText(WidgetTester tester) {
  return tester
      .widgetList<Text>(find.byType(Text))
      .map((text) => text.data ?? '')
      .join(' ');
}
