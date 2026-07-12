import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../widgets/booking_step_header.dart';

class BookingRequestScreen extends StatelessWidget {
  const BookingRequestScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Review booking')),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, _) {
          if (controller.selectedCategory == null ||
              controller.selectedCompanion == null ||
              controller.selectedMeetingSpot == null) {
            return ResponsivePage(
              children: [
                OperationStateView(
                  icon: Icons.edit_calendar_outlined,
                  title: 'Booking details are incomplete',
                  message:
                      'Choose a purpose, companion, and meeting spot before reviewing the booking.',
                  actionLabel: 'Start again',
                  onAction: () => Navigator.of(context).pushNamedAndRemoveUntil(
                    AppRoutes.categorySelection,
                    ModalRoute.withName(AppRoutes.home),
                  ),
                ),
              ],
            );
          }

          return ResponsivePage(
            children: [
              const BookingStepHeader(
                step: 4,
                totalSteps: 4,
                label: 'Set the schedule and review',
              ),
              const SizedBox(height: 20),
              _ReviewPanel(controller: controller),
              const SizedBox(height: 20),
              const PageSectionTitle(title: 'Schedule'),
              const SizedBox(height: 10),
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton.icon(
                      key: const ValueKey('booking-date-button'),
                      onPressed: () => _pickDate(context),
                      icon: const Icon(Icons.calendar_today_outlined),
                      label: Text(_formatDate(controller.startTime)),
                    ),
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: OutlinedButton.icon(
                      key: const ValueKey('booking-time-button'),
                      onPressed: () => _pickTime(context),
                      icon: const Icon(Icons.schedule_outlined),
                      label: Text(_formatTime(controller.startTime)),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              SegmentedButton<int>(
                key: const ValueKey('booking-duration-control'),
                segments: const [
                  ButtonSegment(value: 30, label: Text('30 min')),
                  ButtonSegment(value: 60, label: Text('60 min')),
                  ButtonSegment(value: 90, label: Text('90 min')),
                  ButtonSegment(value: 120, label: Text('120 min')),
                ],
                selected: {controller.durationMinutes},
                showSelectedIcon: false,
                onSelectionChanged: (selection) => controller.setSchedule(
                  start: controller.startTime,
                  duration: selection.single,
                ),
              ),
              const SizedBox(height: 12),
              Text(
                '${_formatDateTime(controller.startTime)} - ${_formatTime(controller.endTime)}',
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.labelLarge,
              ),
              if (controller.bookingError != null) ...[
                const SizedBox(height: 16),
                OperationStateView(
                  icon: Icons.error_outline,
                  title: 'Booking was not submitted',
                  message: controller.bookingError!,
                  isError: true,
                ),
              ],
              const SizedBox(height: 20),
              PrimaryActionButton(
                label: controller.isSubmittingBooking
                    ? 'Submitting...'
                    : 'Request booking',
                icon: Icons.send_outlined,
                onPressed:
                    !controller.hasCompleteDraft ||
                        controller.isSubmittingBooking
                    ? null
                    : () => _submit(context),
              ),
            ],
          );
        },
      ),
    );
  }

  Future<void> _pickDate(BuildContext context) async {
    final now = DateTime.now();
    final picked = await showDatePicker(
      context: context,
      initialDate: controller.startTime,
      firstDate: DateTime(now.year, now.month, now.day),
      lastDate: DateTime(now.year, now.month + 3, now.day),
    );
    if (picked == null) {
      return;
    }
    controller.setSchedule(
      start: DateTime(
        picked.year,
        picked.month,
        picked.day,
        controller.startTime.hour,
        controller.startTime.minute,
      ),
      duration: controller.durationMinutes,
    );
  }

  Future<void> _pickTime(BuildContext context) async {
    final picked = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.fromDateTime(controller.startTime),
    );
    if (picked == null) {
      return;
    }
    controller.setSchedule(
      start: DateTime(
        controller.startTime.year,
        controller.startTime.month,
        controller.startTime.day,
        picked.hour,
        picked.minute,
      ),
      duration: controller.durationMinutes,
    );
  }

  Future<void> _submit(BuildContext context) async {
    final submitted = await controller.submitBooking();
    if (!context.mounted || !submitted) {
      return;
    }
    Navigator.of(context).pushNamedAndRemoveUntil(
      AppRoutes.bookingStatus,
      ModalRoute.withName(AppRoutes.home),
    );
  }

  static String _formatDate(DateTime value) {
    return '${value.day.toString().padLeft(2, '0')}.${value.month.toString().padLeft(2, '0')}.${value.year}';
  }

  static String _formatTime(DateTime value) {
    return '${value.hour.toString().padLeft(2, '0')}:${value.minute.toString().padLeft(2, '0')}';
  }

  static String _formatDateTime(DateTime value) {
    return '${_formatDate(value)} ${_formatTime(value)}';
  }
}

class _ReviewPanel extends StatelessWidget {
  const _ReviewPanel({required this.controller});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        border: Border.all(color: Theme.of(context).colorScheme.outlineVariant),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: [
          _ReviewRow(
            icon: Icons.category_outlined,
            label: 'Purpose',
            value: controller.selectedCategory!.label,
          ),
          const Divider(height: 1),
          _ReviewRow(
            icon: Icons.verified_user_outlined,
            label: 'Companion',
            value: controller.selectedCompanion!.displayName,
          ),
          const Divider(height: 1),
          _ReviewRow(
            icon: Icons.place_outlined,
            label: 'Meeting spot',
            value: controller.selectedMeetingSpot!.name,
          ),
        ],
      ),
    );
  }
}

class _ReviewRow extends StatelessWidget {
  const _ReviewRow({
    required this.icon,
    required this.label,
    required this.value,
  });

  final IconData icon;
  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(14),
      child: Row(
        children: [
          Icon(icon, color: Theme.of(context).colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label, style: Theme.of(context).textTheme.bodySmall),
                const SizedBox(height: 2),
                Text(value, style: Theme.of(context).textTheme.labelLarge),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
