import 'package:flutter/material.dart';

import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/report_reason.dart';

class ReportScreen extends StatefulWidget {
  const ReportScreen({super.key});

  @override
  State<ReportScreen> createState() => _ReportScreenState();
}

class _ReportScreenState extends State<ReportScreen> {
  ReportReason? selectedReason = ReportReason.unsafeBehavior;
  bool blockUser = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Report')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const SafetyNoticeCard(
              title: 'Safety review',
              message: 'Reports can trigger safety review and payout hold.',
              icon: Icons.report_outlined,
            ),
            const SizedBox(height: 12),
            RadioGroup<ReportReason>(
              groupValue: selectedReason,
              onChanged: (value) => setState(() => selectedReason = value),
              child: Column(
                children: [
                  ...ReportReason.values.map(
                    (reason) => Card(
                      child: RadioListTile<ReportReason>(
                        key: ValueKey('report-reason-${reason.apiValue}'),
                        value: reason,
                        title: Text(reason.label),
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
                onChanged: (value) =>
                    setState(() => blockUser = value ?? false),
                title: const Text('Block this user'),
              ),
            ),
            const SizedBox(height: 16),
            PrimaryActionButton(
              label: 'Submit report',
              icon: Icons.report,
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text(
                      'Report will be reviewed by backend safety policy.',
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
}
