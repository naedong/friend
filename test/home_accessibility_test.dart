import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/core/config/app_environment.dart';
import 'package:friend/core/security/dev_actor_config.dart';
import 'package:friend/features/home/home_screen.dart';

import 'test_support/fakes.dart';

void main() {
  testWidgets('Home navigation entries expose button semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();
    final controller = buildTestController();
    addTearDown(controller.dispose);

    await tester.pumpWidget(
      MaterialApp(
        home: HomeScreen(
          environment: _testEnvironment(),
          controller: controller,
        ),
      ),
    );

    for (final label in [
      'Book a public companion, open',
      'View booking status, open',
      'Safety Card, open',
      'Report a problem, open',
      'Profile, open',
    ]) {
      final finder = find.bySemanticsLabel(label);

      expect(finder, findsOneWidget);
      expect(
        tester.getSemantics(finder),
        matchesSemantics(label: label, isButton: true, hasTapAction: true),
      );
    }

    semantics.dispose();
  });
}

AppEnvironment _testEnvironment() {
  return AppEnvironment(
    kind: EnvironmentKind.dev,
    apiBaseUrl: Uri.parse('http://localhost:8080'),
    devActorConfig: DevActorConfig(isProduction: false, enabled: false),
  );
}
