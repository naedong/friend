import 'package:flutter/material.dart';

import '../../app/app_routes.dart';
import '../../core/widgets/primary_action_button.dart';
import '../../core/widgets/safety_notice_card.dart';

class ProductBoundaryScreen extends StatelessWidget {
  const ProductBoundaryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    const boundaryItems = [
      'Friend is for safe public companion bookings.',
      'Not dating.',
      'Not romance.',
      'Not adult service.',
      'Not clubbing or drinking buddy.',
      'Public places only.',
      'Verified companions only.',
      'Report/block available.',
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Friend')),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    const SafetyNoticeCard(
                      title: 'Safe Companion Service',
                      message:
                          'The backend is the source of truth for safety, verification, booking state, and meeting spot approval.',
                    ),
                    const SizedBox(height: 16),
                    ...boundaryItems.map(
                      (item) => Card(
                        child: ListTile(
                          leading: const Icon(Icons.check_circle_outline),
                          title: Text(item),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
              child: PrimaryActionButton(
                label: 'I understand',
                icon: Icons.arrow_forward,
                onPressed: () {
                  Navigator.of(context).pushReplacementNamed(AppRoutes.home);
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
