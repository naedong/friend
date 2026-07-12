import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_options.dart';
import '../widgets/booking_step_header.dart';

class SafeMeetingSpotScreen extends StatelessWidget {
  const SafeMeetingSpotScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Meeting spot')),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, _) {
          final spots = controller.options?.meetingSpots ?? const [];
          return ResponsivePage(
            children: [
              const BookingStepHeader(
                step: 3,
                totalSteps: 4,
                label: 'Choose an approved public place',
              ),
              const SizedBox(height: 20),
              const SafetyNoticeCard(
                title: 'Approved locations only',
                message:
                    'Every location shown here is active, staffed, well-lit, public, and easy to leave.',
              ),
              const SizedBox(height: 16),
              if (spots.isEmpty)
                OperationStateView(
                  icon: Icons.location_off_outlined,
                  title: 'No meeting spots available',
                  message:
                      'No approved public meeting spot is available right now.',
                  actionLabel: 'Refresh',
                  onAction: () => controller.loadBookingOptions(force: true),
                )
              else
                ...spots.map(
                  (spot) => Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: _MeetingSpotCard(
                      meetingSpot: spot,
                      selected: controller.selectedMeetingSpot?.id == spot.id,
                      onTap: () => controller.selectMeetingSpot(spot),
                    ),
                  ),
                ),
              if (spots.isNotEmpty) ...[
                const SizedBox(height: 8),
                PrimaryActionButton(
                  label: 'Review booking',
                  icon: Icons.arrow_forward,
                  onPressed: controller.selectedMeetingSpot == null
                      ? null
                      : () => Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.bookingRequest),
                ),
              ],
            ],
          );
        },
      ),
    );
  }
}

class _MeetingSpotCard extends StatelessWidget {
  const _MeetingSpotCard({
    required this.meetingSpot,
    required this.selected,
    required this.onTap,
  });

  final MeetingSpotOption meetingSpot;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return Semantics(
      button: true,
      selected: selected,
      label:
          '${meetingSpot.name}, ${meetingSpot.address}, ${meetingSpot.typeLabel}, choose meeting spot',
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          color: selected ? colors.primaryContainer : colors.surface,
          child: InkWell(
            key: ValueKey('meeting-spot-${meetingSpot.id}'),
            onTap: onTap,
            borderRadius: BorderRadius.circular(8),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(Icons.place_outlined, color: colors.primary),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          meetingSpot.name,
                          style: Theme.of(context).textTheme.titleSmall,
                        ),
                        const SizedBox(height: 5),
                        Text(meetingSpot.address),
                        const SizedBox(height: 5),
                        Text(
                          meetingSpot.typeLabel,
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ],
                    ),
                  ),
                  Icon(
                    selected
                        ? Icons.radio_button_checked
                        : Icons.radio_button_off,
                    color: colors.primary,
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
