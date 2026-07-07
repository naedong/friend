import 'package:flutter/material.dart';

import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/safety_notice_card.dart';

class CheckInOutScreen extends StatelessWidget {
  const CheckInOutScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Check-in / Check-out')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Active public bookings only',
              message:
                  'Only use this during an active public booking. Location permission must not be requested on screen load.',
            ),
            const SizedBox(height: 12),
            PrimaryActionButton(
              label: 'Check in',
              icon: Icons.login,
              onPressed: () {
                // TODO LOCATION: Request location only after this tap and only for active booking safety.
                _showSafetySnackBar(
                  context,
                  'Check-in will ask for location only when the booking is active.',
                );
              },
            ),
            const SizedBox(height: 8),
            PrimaryActionButton(
              label: 'Check out',
              icon: Icons.logout,
              onPressed: () {
                // TODO LOCATION: Request location only after this tap and only for active booking safety.
                _showSafetySnackBar(
                  context,
                  'Check-out will ask for location only when the booking is active.',
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  void _showSafetySnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(message)));
  }
}
