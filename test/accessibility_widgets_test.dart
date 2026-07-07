import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/core/widgets/primary_action_button.dart';
import 'package:friend/core/widgets/safety_notice_card.dart';

void main() {
  testWidgets('PrimaryActionButton keeps a minimum accessible tap target', (
    tester,
  ) async {
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: Center(
            child: PrimaryActionButton(label: 'Check in', onPressed: () {}),
          ),
        ),
      ),
    );

    expect(
      tester.getSize(find.byType(FilledButton)).height,
      greaterThanOrEqualTo(48),
    );
  });

  testWidgets('SafetyNoticeCard exposes title and message to semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();

    await tester.pumpWidget(
      const MaterialApp(
        home: Scaffold(
          body: SafetyNoticeCard(
            title: 'Public safety first',
            message: 'Stay visible and use Report if anything feels unsafe.',
          ),
        ),
      ),
    );

    expect(
      find.bySemanticsLabel(RegExp('Public safety first')),
      findsOneWidget,
    );
    expect(
      find.bySemanticsLabel(
        RegExp('Stay visible and use Report if anything feels unsafe.'),
      ),
      findsOneWidget,
    );
    semantics.dispose();
  });
}
