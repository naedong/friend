import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/safety_notice_card.dart';

class SafeMeetingSpotScreen extends StatelessWidget {
  const SafeMeetingSpotScreen({super.key});

  @override
  Widget build(BuildContext context) {
    const spots = [
      _MockMeetingSpot(
        'Central Library Cafe',
        'Staffed public cafe inside the main library',
      ),
      _MockMeetingSpot(
        'City Museum Lobby',
        'Public staffed lobby with clear exits',
      ),
      _MockMeetingSpot(
        'Main Station Public Hall',
        'Well-lit public hall near staffed counters',
      ),
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Meeting spot')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Backend approval required',
              message:
                  'Meeting spots must be public, staffed, well-lit, easy to exit, and not alcohol-centered. Final approval comes from the backend.',
            ),
            const SizedBox(height: 12),
            ...spots.map(
              (spot) => Card(
                child: ListTile(
                  leading: const Icon(Icons.place_outlined),
                  title: Text(spot.name),
                  subtitle: Text(spot.description),
                ),
              ),
            ),
            const SizedBox(height: 16),
            PrimaryActionButton(
              label: 'Continue to request',
              icon: Icons.arrow_forward,
              onPressed: () =>
                  Navigator.of(context).pushNamed(AppRoutes.bookingRequest),
            ),
          ],
        ),
      ),
    );
  }
}

class _MockMeetingSpot {
  const _MockMeetingSpot(this.name, this.description);

  final String name;
  final String description;
}
