import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/features/safety_card/screens/safety_card_screen.dart';

void main() {
  testWidgets('Safety Card details expose combined read-only semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();

    await tester.pumpWidget(const MaterialApp(home: SafetyCardScreen()));

    for (final row in const {
      'Safety Card reference': 'SC-PUBLIC-EXAMPLE',
      'Category': 'Coffee Buddy',
      'Meeting spot': 'Central Library Cafe, 1 Public Square',
      'Start / End': 'Tomorrow, 10:00 - 11:00',
      'Companion': 'Display name only',
      'Customer': 'Display name only',
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
}
