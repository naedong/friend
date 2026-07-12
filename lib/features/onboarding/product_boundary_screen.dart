import 'package:flutter/material.dart';

import '../../app/app_routes.dart';
import '../../core/widgets/primary_action_button.dart';

class ProductBoundaryScreen extends StatelessWidget {
  const ProductBoundaryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    const boundaryItems = [
      ('Not dating.', Icons.favorite_border),
      ('Not romance.', Icons.forum_outlined),
      ('Not adult service.', Icons.block_outlined),
      ('Not clubbing or drinking buddy.', Icons.local_bar_outlined),
      ('Public places only.', Icons.public_outlined),
      ('Verified companions only.', Icons.verified_user_outlined),
      ('Report/block available.', Icons.report_outlined),
    ];

    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: Center(
                child: ConstrainedBox(
                  constraints: const BoxConstraints(maxWidth: 720),
                  child: ListView(
                    padding: const EdgeInsets.fromLTRB(20, 28, 20, 20),
                    children: [
                      Container(
                        padding: const EdgeInsets.all(24),
                        decoration: BoxDecoration(
                          color: Theme.of(context).colorScheme.primaryContainer,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(
                              Icons.handshake_outlined,
                              size: 36,
                              color: Theme.of(context).colorScheme.primary,
                            ),
                            const SizedBox(height: 18),
                            Text(
                              'Friend',
                              style: Theme.of(context).textTheme.headlineMedium
                                  ?.copyWith(fontWeight: FontWeight.w800),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              'Friend is for safe public companion bookings.',
                              style: Theme.of(context).textTheme.titleMedium,
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 24),
                      Text(
                        'Service boundary',
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 10),
                      ...boundaryItems.map(
                        (item) => Padding(
                          padding: const EdgeInsets.symmetric(vertical: 5),
                          child: Row(
                            children: [
                              SizedBox.square(
                                dimension: 40,
                                child: Icon(
                                  item.$2,
                                  color: Theme.of(context).colorScheme.primary,
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(child: Text(item.$1)),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 720),
                child: Padding(
                  padding: const EdgeInsets.fromLTRB(20, 8, 20, 20),
                  child: PrimaryActionButton(
                    label: 'I understand',
                    icon: Icons.arrow_forward,
                    onPressed: () {
                      Navigator.of(
                        context,
                      ).pushReplacementNamed(AppRoutes.home);
                    },
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
