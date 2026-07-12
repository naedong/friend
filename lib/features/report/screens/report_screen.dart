import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/report_reason.dart';

class ReportScreen extends StatefulWidget {
  const ReportScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  State<ReportScreen> createState() => _ReportScreenState();
}

class _ReportScreenState extends State<ReportScreen> {
  ReportReason? selectedReason = ReportReason.unsafeBehavior;
  bool blockUser = false;

  @override
  void initState() {
    super.initState();
    widget.controller.clearActionFeedback();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Report')),
      body: ListenableBuilder(
        listenable: widget.controller,
        builder: (context, _) {
          final controller = widget.controller;
          if (controller.activeBooking == null) {
            return ResponsivePage(
              children: [
                OperationStateView(
                  icon: Icons.report_off_outlined,
                  title: 'No booking to report',
                  message:
                      'Reports must be linked to a booking and one of its participants.',
                  actionLabel: 'View bookings',
                  onAction: () =>
                      Navigator.of(context).pushNamed(AppRoutes.bookingStatus),
                ),
              ],
            );
          }

          if (controller.reportSubmitted) {
            return ResponsivePage(
              children: [
                OperationStateView(
                  icon: Icons.verified_outlined,
                  title: 'Report submitted',
                  message:
                      'The safety team can now review the booking. Any requested block is active.',
                ),
                const SizedBox(height: 16),
                PrimaryActionButton(
                  label: 'Return to booking',
                  icon: Icons.arrow_back,
                  onPressed: () => Navigator.of(context).pop(),
                ),
              ],
            );
          }

          return ResponsivePage(
            children: [
              const SafetyNoticeCard(
                title: 'Safety review',
                message:
                    'A report can move the booking into safety review and may freeze payout.',
                icon: Icons.report_outlined,
              ),
              const SizedBox(height: 16),
              Text(
                'What happened?',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              RadioGroup<ReportReason>(
                groupValue: selectedReason,
                onChanged: (value) => setState(() => selectedReason = value),
                child: Column(
                  children: [
                    ...ReportReason.values.map(
                      (reason) => Padding(
                        padding: const EdgeInsets.only(bottom: 8),
                        child: Card(
                          child: RadioListTile<ReportReason>(
                            key: ValueKey('report-reason-${reason.apiValue}'),
                            value: reason,
                            title: Text(reason.label),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Card(
                child: CheckboxListTile(
                  key: const ValueKey('report-block-user'),
                  value: blockUser,
                  onChanged: controller.isPerformingAction
                      ? null
                      : (value) => setState(() => blockUser = value ?? false),
                  title: const Text('Block this user'),
                  subtitle: const Text('Prevent future bookings together'),
                ),
              ),
              if (controller.actionError != null) ...[
                const SizedBox(height: 16),
                OperationStateView(
                  icon: Icons.error_outline,
                  title: 'Report was not submitted',
                  message: controller.actionError!,
                  isError: true,
                ),
              ],
              const SizedBox(height: 20),
              PrimaryActionButton(
                label: controller.isPerformingAction
                    ? 'Submitting...'
                    : 'Submit report',
                icon: Icons.report,
                onPressed:
                    selectedReason == null || controller.isPerformingAction
                    ? null
                    : () => controller.submitReport(
                        reason: selectedReason!,
                        blockReportedUser: blockUser,
                      ),
              ),
            ],
          );
        },
      ),
    );
  }
}
