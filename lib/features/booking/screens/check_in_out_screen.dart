import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_response.dart';

class CheckInOutScreen extends StatelessWidget {
  const CheckInOutScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Check-in / Check-out')),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, _) {
          final booking = controller.activeBooking;
          if (booking == null) {
            return ResponsivePage(
              children: [
                OperationStateView(
                  icon: Icons.event_busy_outlined,
                  title: 'No booking to check in',
                  message:
                      'Create a booking before using check-in or check-out.',
                  actionLabel: 'Start booking',
                  onAction: () => Navigator.of(
                    context,
                  ).pushNamed(AppRoutes.categorySelection),
                ),
              ],
            );
          }

          final canCheckIn =
              booking.status == BookingStatusLabel.accepted ||
              booking.status == BookingStatusLabel.checkedIn;
          final canCheckOut =
              booking.status == BookingStatusLabel.inProgress ||
              booking.status == BookingStatusLabel.checkoutPending;

          return ResponsivePage(
            children: [
              const SafetyNoticeCard(
                title: 'Active public bookings only',
                message:
                    'Confirm that you are at the approved public meeting spot before recording an action.',
              ),
              const SizedBox(height: 20),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.surface,
                  border: Border.all(
                    color: Theme.of(context).colorScheme.outlineVariant,
                  ),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.fact_check_outlined,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            booking.category.label,
                            style: Theme.of(context).textTheme.titleSmall,
                          ),
                          const SizedBox(height: 4),
                          Text('Status: ${booking.status.label}'),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              if (controller.actionError != null) ...[
                const SizedBox(height: 16),
                OperationStateView(
                  icon: Icons.error_outline,
                  title: 'Action was not recorded',
                  message: controller.actionError!,
                  isError: true,
                ),
              ],
              const SizedBox(height: 20),
              PrimaryActionButton(
                label: controller.isPerformingAction
                    ? 'Recording...'
                    : 'Check in',
                icon: Icons.login,
                onPressed: canCheckIn && !controller.isPerformingAction
                    ? () => _perform(context, controller.checkIn)
                    : null,
              ),
              const SizedBox(height: 10),
              PrimaryActionButton(
                label: controller.isPerformingAction
                    ? 'Recording...'
                    : 'Check out',
                icon: Icons.logout,
                onPressed: canCheckOut && !controller.isPerformingAction
                    ? () => _perform(context, controller.checkOut)
                    : null,
              ),
              const SizedBox(height: 12),
              Text(
                canCheckIn || canCheckOut
                    ? 'Location is not collected until a reviewed permission flow is implemented.'
                    : 'These actions become available when the backend advances the booking status.',
                style: Theme.of(context).textTheme.bodySmall,
                textAlign: TextAlign.center,
              ),
            ],
          );
        },
      ),
    );
  }

  Future<void> _perform(
    BuildContext context,
    Future<bool> Function() action,
  ) async {
    final completed = await action();
    if (!context.mounted || !completed) {
      return;
    }
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(const SnackBar(content: Text('Booking status updated.')));
  }
}
