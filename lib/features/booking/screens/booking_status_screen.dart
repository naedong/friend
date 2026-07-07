import 'package:flutter/material.dart';

import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_response.dart';

class BookingStatusScreen extends StatelessWidget {
  const BookingStatusScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Booking status')),
      body: SafeArea(
        child: ListView.separated(
          padding: const EdgeInsets.all(16),
          itemCount: BookingStatusLabel.values.length + 1,
          separatorBuilder: (_, _) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            if (index == 0) {
              return const SafetyNoticeCard(
                title: 'Status is server-controlled',
                message:
                    'If anything feels unsafe, use Report immediately. Stay in the public meeting spot.',
              );
            }
            final status = BookingStatusLabel.values[index - 1];
            return Card(
              child: ListTile(
                leading: const Icon(Icons.radio_button_checked),
                title: Text(status.label),
                subtitle: Text(status.apiValue),
              ),
            );
          },
        ),
      ),
    );
  }
}
