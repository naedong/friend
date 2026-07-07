import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:friend/features/booking/models/booking_category.dart';
import 'package:friend/features/booking/screens/category_selection_screen.dart';

void main() {
  testWidgets('Category selection entries expose button semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();

    await tester.pumpWidget(const MaterialApp(home: CategorySelectionScreen()));

    for (final category in allowedBookingCategories) {
      await tester.ensureVisible(find.text(category.label));
      await tester.pump();

      final label =
          '${category.label}, ${category.safetyDescription}, choose category';
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
