import 'package:flutter/material.dart';

import '../../../app/app_routes.dart';
import '../../../app/friend_app_controller.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/booking_category.dart';
import '../widgets/booking_step_header.dart';

class CategorySelectionScreen extends StatelessWidget {
  const CategorySelectionScreen({required this.controller, super.key});

  final FriendAppController controller;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Booking purpose')),
      body: ResponsivePage(
        children: [
          const BookingStepHeader(
            step: 1,
            totalSteps: 4,
            label: 'Choose a booking purpose',
          ),
          const SizedBox(height: 20),
          const SafetyNoticeCard(
            title: 'Purpose-based bookings',
            message:
                'Every available category is limited to a public, time-boxed activity.',
          ),
          const SizedBox(height: 16),
          ...allowedBookingCategories.map(
            (category) => Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: _CategoryCard(
                category: category,
                selected: controller.selectedCategory == category,
                onTap: () {
                  controller.selectCategory(category);
                  Navigator.of(context).pushNamed(AppRoutes.companionSelection);
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _CategoryCard extends StatelessWidget {
  const _CategoryCard({
    required this.category,
    required this.selected,
    required this.onTap,
  });

  final BookingCategory category;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    final label =
        '${category.label}, ${category.safetyDescription}, choose category';
    return Semantics(
      button: true,
      selected: selected,
      label: label,
      onTap: onTap,
      child: ExcludeSemantics(
        child: Card(
          color: selected ? colors.primaryContainer : colors.surface,
          child: InkWell(
            key: ValueKey('category-${category.apiValue}'),
            onTap: onTap,
            borderRadius: BorderRadius.circular(8),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    width: 42,
                    height: 42,
                    decoration: BoxDecoration(
                      color: colors.primaryContainer,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Icon(_iconFor(category), color: colors.primary),
                  ),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          category.label,
                          style: Theme.of(context).textTheme.titleSmall,
                        ),
                        const SizedBox(height: 5),
                        Text(
                          category.safetyDescription,
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(width: 8),
                  Icon(
                    selected ? Icons.check_circle : Icons.chevron_right,
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

  IconData _iconFor(BookingCategory value) {
    return switch (value) {
      BookingCategory.coffeeBuddy => Icons.local_cafe_outlined,
      BookingCategory.museumExhibitionBuddy => Icons.museum_outlined,
      BookingCategory.cityWalkBuddy => Icons.directions_walk_outlined,
      BookingCategory.languagePracticeBuddy => Icons.translate_outlined,
      BookingCategory.safeTradeBuddy => Icons.handshake_outlined,
      BookingCategory.adminAppointmentBuddy => Icons.apartment_outlined,
    };
  }
}
