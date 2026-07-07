import 'package:flutter/material.dart';

import '../../../core/widgets/safety_notice_card.dart';

class SafetyCardScreen extends StatelessWidget {
  const SafetyCardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    const rows = [
      _SafetyCardRow('Safety Card reference', 'SC-PUBLIC-EXAMPLE'),
      _SafetyCardRow('Category', 'Coffee Buddy'),
      _SafetyCardRow('Meeting spot', 'Central Library Cafe, 1 Public Square'),
      _SafetyCardRow('Start / End', 'Tomorrow, 10:00 - 11:00'),
      _SafetyCardRow('Companion', 'Display name only'),
      _SafetyCardRow('Customer', 'Display name only'),
      _SafetyCardRow('Verification summary', 'Identity and liveness verified'),
      _SafetyCardRow(
        'Instructions',
        'Stay public. Use Report if anything feels unsafe.',
      ),
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Safety Card')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Limited public view',
              message:
                  'Safety Cards show a public reference and limited booking details. They do not show user IDs, email, phone, provider references, private profile data, or audit information.',
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

class _SafetyCardRow {
  const _SafetyCardRow(this.label, this.value);

  final String label;
  final String value;
}
