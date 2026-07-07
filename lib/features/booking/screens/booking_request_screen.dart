import 'package:flutter/material.dart';

import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_category.dart';

class BookingRequestScreen extends StatelessWidget {
  const BookingRequestScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final startTime = DateTime.now().add(const Duration(days: 1, hours: 2));
    final endTime = startTime.add(const Duration(hours: 1));

    return Scaffold(
      appBar: AppBar(title: const Text('Request booking')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Safety reminder',
              message:
                  'The request sends only companion, category, meeting spot, start time, and end time. The backend controls status and safety checks.',
            ),
            const SizedBox(height: 12),
            _DetailRow(
              label: 'Selected category',
              value: BookingCategory.coffeeBuddy.label,
            ),
            const _DetailRow(
              label: 'Selected meeting spot',
              value: 'Central Library Cafe',
            ),
            _DetailRow(label: 'Start time', value: _formatDateTime(startTime)),
            _DetailRow(label: 'End time', value: _formatDateTime(endTime)),
            const SizedBox(height: 16),
            PrimaryActionButton(
              label: 'Request booking',
              icon: Icons.send_outlined,
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text(
                      'Booking request will be validated by the backend.',
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  String _formatDateTime(DateTime value) {
    final local = value.toLocal();
    return '${local.year}-${local.month.toString().padLeft(2, '0')}-${local.day.toString().padLeft(2, '0')} '
        '${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}

class _DetailRow extends StatelessWidget {
  const _DetailRow({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(title: Text(label), subtitle: Text(value)),
    );
  }
}
