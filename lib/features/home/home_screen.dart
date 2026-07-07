import 'package:flutter/material.dart';

import '../../app/app_routes.dart';
import '../../core/config/app_environment.dart';
import '../../core/widgets/safety_notice_card.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({required this.environment, super.key});

  final AppEnvironment environment;

  @override
  Widget build(BuildContext context) {
    const entries = [
      _HomeEntry(
        'Book a public companion',
        Icons.public_outlined,
        AppRoutes.categorySelection,
      ),
      _HomeEntry(
        'View booking status',
        Icons.fact_check_outlined,
        AppRoutes.bookingStatus,
      ),
      _HomeEntry(
        'Safety Card',
        Icons.health_and_safety_outlined,
        AppRoutes.safetyCard,
      ),
      _HomeEntry('Report a problem', Icons.report_outlined, AppRoutes.report),
      _HomeEntry('Profile', Icons.person_outline, AppRoutes.profile),
    ];

    return Scaffold(
      appBar: AppBar(title: const Text('Home')),
      body: SafeArea(
        child: ListView.separated(
          padding: const EdgeInsets.all(16),
          itemBuilder: (context, index) {
            if (index == 0) {
              return SafetyNoticeCard(
                title: 'Public safety first',
                message:
                    'Environment: ${environment.kind.name}. Stay in public places and use Report if anything feels unsafe.',
              );
            }
            final entry = entries[index - 1];
            return _HomeEntryCard(
              entry: entry,
              onTap: () => Navigator.of(context).pushNamed(entry.route),
            );
          },
          separatorBuilder: (_, _) => const SizedBox(height: 8),
          itemCount: entries.length + 1,
        ),
      ),
    );
  }
}

class _HomeEntryCard extends StatelessWidget {
  const _HomeEntryCard({required this.entry, required this.onTap});

  final _HomeEntry entry;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      button: true,
      label: '${entry.label}, open',
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          child: ListTile(
            leading: Icon(entry.icon),
            title: Text(entry.label),
            trailing: const Icon(Icons.chevron_right),
            onTap: onTap,
          ),
        ),
      ),
    );
  }
}

class _HomeEntry {
  const _HomeEntry(this.label, this.icon, this.route);

  final String label;
  final IconData icon;
  final String route;
}
