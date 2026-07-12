import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/responsive_page.dart';
import '../models/booking_options.dart';
import '../widgets/booking_step_header.dart';

class CompanionSelectionScreen extends StatefulWidget {
  const CompanionSelectionScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  State<CompanionSelectionScreen> createState() =>
      _CompanionSelectionScreenState();
}

class _CompanionSelectionScreenState extends State<CompanionSelectionScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        widget.controller.loadBookingOptions();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Verified companion')),
      body: ListenableBuilder(
        listenable: widget.controller,
        builder: (context, _) {
          final controller = widget.controller;
          return ResponsivePage(
            children: [
              const BookingStepHeader(
                step: 2,
                totalSteps: 4,
                label: 'Choose a verified companion',
              ),
              const SizedBox(height: 20),
              if (controller.isLoadingOptions)
                const _LoadingOptions()
              else if (controller.optionsError != null)
                OperationStateView(
                  icon: Icons.cloud_off_outlined,
                  title: 'Could not load companions',
                  message: controller.optionsError!,
                  actionLabel: 'Retry',
                  onAction: () => controller.loadBookingOptions(force: true),
                  isError: true,
                )
              else if (controller.options?.companions.isEmpty ?? true)
                OperationStateView(
                  icon: Icons.person_search_outlined,
                  title: 'No companions available',
                  message:
                      'There are no verified companions available for a new booking right now.',
                  actionLabel: 'Refresh',
                  onAction: () => controller.loadBookingOptions(force: true),
                )
              else
                ...controller.options!.companions.map(
                  (companion) => Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: _CompanionCard(
                      companion: companion,
                      selected:
                          controller.selectedCompanion?.id == companion.id,
                      onTap: () {
                        controller.selectCompanion(companion);
                        Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.safeMeetingSpot);
                      },
                    ),
                  ),
                ),
            ],
          );
        },
      ),
    );
  }
}

class _LoadingOptions extends StatelessWidget {
  const _LoadingOptions();

  @override
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.symmetric(vertical: 36),
      child: Column(
        children: [
          CircularProgressIndicator(),
          SizedBox(height: 16),
          Text('Finding verified companions...'),
        ],
      ),
    );
  }
}

class _CompanionCard extends StatelessWidget {
  const _CompanionCard({
    required this.companion,
    required this.selected,
    required this.onTap,
  });

  final CompanionOption companion;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return Semantics(
      button: true,
      selected: selected,
      label:
          '${companion.displayName}, identity and liveness verified, choose companion',
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          color: selected ? colors.primaryContainer : colors.surface,
          child: InkWell(
            key: ValueKey('companion-${companion.id}'),
            onTap: onTap,
            borderRadius: BorderRadius.circular(8),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CircleAvatar(
                    backgroundColor: colors.primaryContainer,
                    foregroundColor: colors.primary,
                    child: Text(
                      companion.displayName.isEmpty
                          ? '?'
                          : companion.displayName.substring(0, 1),
                    ),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          companion.displayName,
                          style: Theme.of(context).textTheme.titleSmall,
                        ),
                        const SizedBox(height: 5),
                        Row(
                          children: [
                            Icon(
                              Icons.verified_outlined,
                              size: 18,
                              color: colors.primary,
                            ),
                            const SizedBox(width: 5),
                            const Expanded(
                              child: Text('Identity and liveness verified'),
                            ),
                          ],
                        ),
                        if (companion.bio.isNotEmpty) ...[
                          const SizedBox(height: 8),
                          Text(
                            companion.bio,
                            style: Theme.of(context).textTheme.bodySmall,
                          ),
                        ],
                      ],
                    ),
                  ),
                  const SizedBox(width: 8),
                  Icon(Icons.chevron_right, color: colors.primary),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
