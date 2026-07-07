import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_category.dart';

class CategorySelectionScreen extends StatelessWidget {
  const CategorySelectionScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Choose category')),
      body: SafeArea(
        child: ListView.separated(
          padding: const EdgeInsets.all(16),
          itemCount: allowedBookingCategories.length + 1,
          separatorBuilder: (_, _) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            if (index == 0) {
              return const SafetyNoticeCard(
                title: 'Allowed MVP categories only',
                message:
                    'Every category is public, purpose-based, and validated again by the backend.',
              );
            }
            final category = allowedBookingCategories[index - 1];
            return _CategoryCard(
              category: category,
              onTap: () =>
                  Navigator.of(context).pushNamed(AppRoutes.safeMeetingSpot),
            );
          },
        ),
      ),
    );
  }
}

class _CategoryCard extends StatelessWidget {
  const _CategoryCard({required this.category, required this.onTap});

  final BookingCategory category;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      button: true,
      label:
          '${category.label}, ${category.safetyDescription}, choose category',
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          child: ListTile(
            leading: const Icon(Icons.public_outlined),
            title: Text(category.label),
            subtitle: Text(category.safetyDescription),
            trailing: const Icon(Icons.chevron_right),
            onTap: onTap,
          ),
        ),
      ),
    );
  }
}
