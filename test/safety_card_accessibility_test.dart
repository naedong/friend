import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/features/safety_card/screens/safety_card_screen.dart';

import 'test_support/fakes.dart';

void main() {
  testWidgets('Safety Card details expose combined read-only semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();

    await tester.pumpWidget(
      MaterialApp(
        home: SafetyCardScreen(
          gateway: FakeSafetyCardGateway(),
          initialView: testSafetyCardView,
        ),
      ),
    );

    for (final row in const {
      'Safety Card reference': 'SC-PUBLIC-EXAMPLE',
      'Category': 'Coffee Buddy',
      'Meeting spot': 'Central Library Cafe, 1 Public Square',
      'Start / End': '13.07.2026 10:00 - 11:00',
      'Companion': 'Mina Park',
      'Customer': 'Alex Morgan',
      'Verification summary': 'Identity and liveness verified',
      'Instructions': 'Stay public. Use Report if anything feels unsafe.',
    }.entries) {
      await tester.ensureVisible(find.text(row.key));
      await tester.pumpAndSettle();

      final label = '${row.key}: ${row.value}';
      final finder = find.bySemanticsLabel(label);

      expect(finder, findsOneWidget);
      expect(
        tester.getSemantics(finder),
        matchesSemantics(label: label, isReadOnly: true),
      );
    }

    semantics.dispose();
  });

  testWidgets('valid public token loads a Safety Card from the gateway', (
    tester,
  ) async {
    final gateway = FakeSafetyCardGateway();

    await tester.pumpWidget(
      MaterialApp(home: SafetyCardScreen(gateway: gateway)),
    );

    await tester.enterText(
      find.byKey(const ValueKey('safety-card-token-field')),
      'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ',
    );
    await tester.tap(find.text('Open Safety Card'));
    await tester.pumpAndSettle();

    expect(gateway.calls, 1);
    expect(find.text('SC-PUBLIC-EXAMPLE'), findsWidgets);
    await tester.scrollUntilVisible(find.text('Mina Park'), 200);
    expect(find.text('Mina Park'), findsOneWidget);
  });
}
