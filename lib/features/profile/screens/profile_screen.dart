import 'package:flutter/material.dart';

import '../../../app/friend_app_controller.dart';
import '../../../core/config/app_environment.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({
    required this.environment,
    required this.controller,
    super.key,
  });

  final AppEnvironment environment;
  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ResponsivePage(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.primaryContainer,
              borderRadius: BorderRadius.circular(8),
            ),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 28,
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  foregroundColor: Theme.of(context).colorScheme.onPrimary,
                  child: const Icon(Icons.person_outline),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        environment.kind == EnvironmentKind.dev
                            ? 'Local development account'
                            : 'Friend account',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 4),
                      Text('Environment: ${environment.kind.name}'),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),
          const PageSectionTitle(title: 'Account security'),
          const SizedBox(height: 10),
          Card(
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.verified_user_outlined),
                  title: const Text('Authentication'),
                  subtitle: Text(
                    environment.kind == EnvironmentKind.dev &&
                            environment.devActorConfig.enabled
                        ? 'Local development identity is connected'
                        : 'Production sign-in is required',
                  ),
                ),
                const Divider(height: 1),
                const ListTile(
                  leading: Icon(Icons.lock_outline),
                  title: Text('Role and verification'),
                  subtitle: Text('Managed by the Friend backend'),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),
          const SafetyNoticeCard(
            title: 'Private by default',
            message:
                'Identity evidence, phone numbers, provider references, and audit records are never shown on public Safety Cards.',
          ),
        ],
      ),
    );
  }
}
