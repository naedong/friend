import 'package:flutter/material.dart';

class BookingStepHeader extends StatelessWidget {
  const BookingStepHeader({
    required this.step,
    required this.totalSteps,
    required this.label,
    super.key,
  });

  final int step;
  final int totalSteps;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      label: 'Booking step $step of $totalSteps: $label',
      child: ExcludeSemantics(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Step $step of $totalSteps',
              style: Theme.of(context).textTheme.labelLarge?.copyWith(
                color: Theme.of(context).colorScheme.primary,
              ),
            ),
            const SizedBox(height: 8),
            LinearProgressIndicator(
              value: step / totalSteps,
              minHeight: 6,
              borderRadius: BorderRadius.circular(3),
            ),
          ],
        ),
      ),
    );
  }
}
