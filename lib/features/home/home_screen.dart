import 'package:flutter/material.dart';

import '../../app/app_routes.dart';
import '../../app/friend_app_controller.dart';
import '../../core/config/app_environment.dart';
import '../../core/widgets/operation_state_view.dart';
import '../../core/widgets/responsive_page.dart';
import '../../core/widgets/safety_notice_card.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({
    required this.environment,
    required this.controller,
    super.key,
  });

  final AppEnvironment environment;
  final FriendAppController controller;

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        widget.controller.loadLatestBooking();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final controller = widget.controller;
    final environment = widget.environment;
    return Scaffold(
      appBar: AppBar(
        title: const Text('Friend'),
        actions: [
          IconButton(
            tooltip: 'Profile',
            onPressed: () => Navigator.of(context).pushNamed(AppRoutes.profile),
            icon: const Icon(Icons.account_circle_outlined),
          ),
          const SizedBox(width: 4),
        ],
      ),
      body: ListenableBuilder(
        listenable: controller,
        builder: (context, _) {
          final booking = controller.activeBooking;
          return ResponsivePage(
            children: [
              Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primaryContainer,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(
                          Icons.shield_outlined,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          environment.kind == EnvironmentKind.dev
                              ? 'Development connection'
                              : 'Secure connection',
                          style: Theme.of(context).textTheme.labelLarge,
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Text(
                      booking == null
                          ? 'Plan a safe public booking'
                          : 'Your booking is ${booking.status.label.toLowerCase()}',
                      style: Theme.of(context).textTheme.headlineSmall
                          ?.copyWith(fontWeight: FontWeight.w800),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      booking == null
                          ? 'Choose a purpose, a verified companion, and an approved meeting spot.'
                          : '${booking.category.label} · ${_formatDateTime(booking.startTime)}',
                    ),
                    const SizedBox(height: 18),
                    FilledButton.icon(
                      onPressed: () => Navigator.of(context).pushNamed(
                        booking == null
                            ? AppRoutes.categorySelection
                            : AppRoutes.bookingStatus,
                      ),
                      icon: Icon(
                        booking == null
                            ? Icons.add_circle_outline
                            : Icons.fact_check_outlined,
                      ),
                      label: Text(
                        booking == null ? 'Start booking' : 'View booking',
                      ),
                    ),
                  ],
                ),
              ),
              if (controller.latestBookingError != null) ...[
                const SizedBox(height: 12),
                OperationStateView(
                  icon: Icons.cloud_off_outlined,
                  title: 'Booking status is offline',
                  message: controller.latestBookingError!,
                  actionLabel: 'Retry',
                  onAction: () => controller.loadLatestBooking(force: true),
                  isError: true,
                ),
              ],
              const SizedBox(height: 24),
              const PageSectionTitle(title: 'Actions'),
              const SizedBox(height: 10),
              LayoutBuilder(
                builder: (context, constraints) {
                  final columns = constraints.maxWidth >= 620 ? 3 : 2;
                  return GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: columns,
                    mainAxisSpacing: 10,
                    crossAxisSpacing: 10,
                    childAspectRatio: columns == 3 ? 1.7 : 1.25,
                    children: [
                      _ActionTile(
                        label: 'Book a public companion',
                        icon: Icons.public_outlined,
                        onTap: () => Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.categorySelection),
                      ),
                      _ActionTile(
                        label: 'View booking status',
                        icon: Icons.fact_check_outlined,
                        onTap: () => Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.bookingStatus),
                      ),
                      _ActionTile(
                        label: 'Check-in / Check-out',
                        icon: Icons.login_outlined,
                        onTap: () => Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.checkInOut),
                      ),
                      _ActionTile(
                        label: 'Safety Card',
                        icon: Icons.health_and_safety_outlined,
                        onTap: () => Navigator.of(
                          context,
                        ).pushNamed(AppRoutes.safetyCard),
                      ),
                      _ActionTile(
                        label: 'Report a problem',
                        icon: Icons.report_outlined,
                        isUrgent: true,
                        onTap: () =>
                            Navigator.of(context).pushNamed(AppRoutes.report),
                      ),
                      _ActionTile(
                        label: 'Profile',
                        icon: Icons.person_outline,
                        onTap: () =>
                            Navigator.of(context).pushNamed(AppRoutes.profile),
                      ),
                    ],
                  );
                },
              ),
              const SizedBox(height: 24),
              const SafetyNoticeCard(
                title: 'Public safety first',
                message:
                    'Stay in the approved public meeting spot. Use Report immediately if anything feels unsafe.',
              ),
            ],
          );
        },
      ),
    );
  }

  String _formatDateTime(DateTime value) {
    final local = value.toLocal();
    return '${local.day.toString().padLeft(2, '0')}.${local.month.toString().padLeft(2, '0')} '
        '${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}

class _ActionTile extends StatelessWidget {
  const _ActionTile({
    required this.label,
    required this.icon,
    required this.onTap,
    this.isUrgent = false,
  });

  final String label;
  final IconData icon;
  final VoidCallback onTap;
  final bool isUrgent;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return Semantics(
      button: true,
      label: '$label, open',
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          color: isUrgent ? colors.errorContainer : colors.surface,
          child: InkWell(
            onTap: onTap,
            borderRadius: BorderRadius.circular(8),
            child: Padding(
              padding: const EdgeInsets.all(14),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(icon, color: isUrgent ? colors.error : colors.primary),
                  const Spacer(),
                  Text(
                    label,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: Theme.of(context).textTheme.labelLarge,
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
