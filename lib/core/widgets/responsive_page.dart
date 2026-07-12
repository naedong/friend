import 'package:flutter/material.dart';

class ResponsivePage extends StatelessWidget {
  const ResponsivePage({
    required this.children,
    this.padding = const EdgeInsets.fromLTRB(16, 12, 16, 24),
    this.maxWidth = 760,
    this.controller,
    super.key,
  });

  final List<Widget> children;
  final EdgeInsets padding;
  final double maxWidth;
  final ScrollController? controller;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Center(
        child: ConstrainedBox(
          constraints: BoxConstraints(maxWidth: maxWidth),
          child: ListView(
            controller: controller,
            padding: padding,
            children: children,
          ),
        ),
      ),
    );
  }
}

class PageSectionTitle extends StatelessWidget {
  const PageSectionTitle({required this.title, this.trailing, super.key});

  final String title;
  final Widget? trailing;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: Text(title, style: Theme.of(context).textTheme.titleMedium),
        ),
        ?trailing,
      ],
    );
  }
}
