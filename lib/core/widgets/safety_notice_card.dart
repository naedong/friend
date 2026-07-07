import 'package:flutter/material.dart';

class SafetyNoticeCard extends StatelessWidget {
  const SafetyNoticeCard({
    required this.title,
    required this.message,
    this.icon = Icons.health_and_safety_outlined,
    super.key,
  });

  final String title;
  final String message;
  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return MergeSemantics(
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ExcludeSemantics(child: Icon(icon)),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: Theme.of(context).textTheme.titleMedium),
                    const SizedBox(height: 6),
                    Text(message),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
