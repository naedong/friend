import 'package:flutter/material.dart';

import '../../../core/widgets/safety_notice_card.dart';

class ProfilePlaceholderScreen extends StatelessWidget {
  const ProfilePlaceholderScreen({super.key});

  @override
  Widget build(BuildContext context) {
    const rows = [
      _ProfileRow('Display name', 'Provided by account profile'),
      _ProfileRow('Verification status', 'Read-only backend status'),
      _ProfileRow('Companion approval', 'Read-only backend review status'),
      _ProfileRow(
        'Safety reminder',
        'Roles and verification cannot be edited here',
      ),
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Profile placeholder',
              message:
                  'This screen does not edit role, verification status, or companion approval.',
            ),
            const SizedBox(height: 12),
            ...rows.map(
              (row) => Card(
                child: ListTile(
                  title: Text(row.label),
                  subtitle: Text(row.value),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ProfileRow {
  const _ProfileRow(this.label, this.value);

  final String label;
  final String value;
}
