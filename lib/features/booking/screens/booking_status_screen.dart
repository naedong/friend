import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_response.dart';

class BookingStatusScreen extends StatelessWidget {
  const BookingStatusScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Booking status')),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, _) {
          final booking = controller.activeBooking;
          if (booking == null) {
            return ResponsivePage(
              children: [
                OperationStateView(
                  icon: Icons.event_busy_outlined,
                  title: 'No active booking',
                  message:
                      'Create a booking to track its server-controlled status here.',
                  actionLabel: 'Start booking',
                  onAction: () => Navigator.of(
                    context,
                  ).pushNamed(AppRoutes.categorySelection),
                ),
              ],
            );
          }

          return ResponsivePage(
            children: [
              _StatusBanner(status: booking.status),
              const SizedBox(height: 20),
              const PageSectionTitle(title: 'Booking details'),
              const SizedBox(height: 10),
              Container(
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surface,
                  border: Border.all(
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    _DetailRow(label: 'Purpose', value: booking.category.label),
                    const Divider(height: 1),
                    _DetailRow(
                      label: 'Starts',
                      value: _formatDateTime(booking.startTime),
                    ),
                    const Divider(height: 1),
                    _DetailRow(
                      label: 'Ends',
                      value: _formatDateTime(booking.endTime),
                    ),
                    const Divider(height: 1),
                    _DetailRow(
                      label: 'Reference',
                      value: _shortReference(booking.id),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              _NextStep(status: booking.status),
              if (_supportsCheckAction(booking.status)) ...[
                const SizedBox(height: 16),
                PrimaryActionButton(
                  label:
                      booking.status == BookingStatusLabel.accepted ||
                          booking.status == BookingStatusLabel.checkedIn
                      ? 'Open check-in'
                      : 'Open check-out',
                  icon:
                      booking.status == BookingStatusLabel.accepted ||
                          booking.status == BookingStatusLabel.checkedIn
                      ? Icons.login
                      : Icons.logout,
                  onPressed: () =>
                      Navigator.of(context).pushNamed(AppRoutes.checkInOut),
                ),
              ],
              const SizedBox(height: 10),
              OutlinedButton.icon(
                onPressed: () =>
                    Navigator.of(context).pushNamed(AppRoutes.report),
                icon: const Icon(Icons.report_outlined),
                label: const Text('Report a problem'),
              ),
              const SizedBox(height: 20),
              const SafetyNoticeCard(
                title: 'Status is server-controlled',
                message:
                    'Stay at the approved public meeting spot and report anything unsafe immediately.',
              ),
            ],
          );
        },
      ),
    );
  }

  static bool _supportsCheckAction(BookingStatusLabel status) {
    return status == BookingStatusLabel.accepted ||
        status == BookingStatusLabel.checkedIn ||
        status == BookingStatusLabel.inProgress ||
        status == BookingStatusLabel.checkoutPending;
  }

  static String _shortReference(String id) {
    return id.length <= 8 ? id : id.substring(0, 8).toUpperCase();
  }

  static String _formatDateTime(DateTime value) {
    final local = value.toLocal();
    return '${local.day.toString().padLeft(2, '0')}.${local.month.toString().padLeft(2, '0')}.${local.year} '
        '${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}

class _StatusBanner extends StatelessWidget {
  const _StatusBanner({required this.status});

  final BookingStatusLabel status;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    final isSafetyState =
        status == BookingStatusLabel.reported ||
        status == BookingStatusLabel.safetyHold ||
        status == BookingStatusLabel.payoutHold;
    final isClosed =
        status == BookingStatusLabel.rejected ||
        status == BookingStatusLabel.cancelled;
    final background = isSafetyState || isClosed
        ? colors.errorContainer
        : colors.primaryContainer;
    final foreground = isSafetyState || isClosed
        ? colors.onErrorContainer
        : colors.onPrimaryContainer;

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: background,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(
            isSafetyState
                ? Icons.warning_amber_outlined
                : Icons.event_available,
            color: foreground,
            size: 30,
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Current status', style: TextStyle(color: foreground)),
                const SizedBox(height: 4),
                Text(
                  status.label,
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    color: foreground,
                    fontWeight: FontWeight.w800,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  const _DetailRow({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 13),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          const SizedBox(width: 12),
          Flexible(
            child: Text(
              value,
              textAlign: TextAlign.end,
              style: Theme.of(context).textTheme.labelLarge,
            ),
          ),
        ],
      ),
    );
  }
}

class _NextStep extends StatelessWidget {
  const _NextStep({required this.status});

  final BookingStatusLabel status;

  @override
  Widget build(BuildContext context) {
    final message = switch (status) {
      BookingStatusLabel.requested =>
        'Waiting for the selected companion to accept the request.',
      BookingStatusLabel.accepted =>
        'Arrive at the approved meeting spot, then check in.',
      BookingStatusLabel.checkedIn =>
        'Waiting for both participants to complete check-in.',
      BookingStatusLabel.inProgress =>
        'The booking is active. Check out before leaving the meeting spot.',
      BookingStatusLabel.checkoutPending =>
        'Waiting for both participants to complete check-out.',
      BookingStatusLabel.completed => 'This booking is complete.',
      BookingStatusLabel.rejected => 'The companion rejected this request.',
      BookingStatusLabel.cancelled => 'This booking was cancelled.',
      BookingStatusLabel.reported ||
      BookingStatusLabel.safetyHold ||
      BookingStatusLabel.payoutHold =>
        'Safety review is active. Follow the instructions from Friend support.',
    };

    return OperationStateView(
      icon: Icons.arrow_circle_right_outlined,
      title: 'Next step',
      message: message,
    );
  }
}
