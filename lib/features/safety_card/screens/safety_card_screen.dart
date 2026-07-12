import 'package:flutter/material.dart';

import '../../../core/network/api_exception.dart';
import '../../../core/widgets/operation_state_view.dart';
import '../../../core/widgets/primary_action_button.dart';
import '../../../core/widgets/responsive_page.dart';
import '../../../core/widgets/safety_notice_card.dart';
import '../models/safety_card_view.dart';
import '../services/safety_card_api.dart';

class SafetyCardScreen extends StatefulWidget {
  const SafetyCardScreen({required this.gateway, this.initialView, super.key});

  final SafetyCardGateway gateway;
  final SafetyCardView? initialView;

  @override
  State<SafetyCardScreen> createState() => _SafetyCardScreenState();
}

class _SafetyCardScreenState extends State<SafetyCardScreen> {
  static final RegExp _tokenPattern = RegExp(
    r'^(?:[A-Za-z0-9_-]{43}|[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$',
  );

  final TextEditingController _tokenController = TextEditingController();
  SafetyCardView? _view;
  bool _isLoading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _view = widget.initialView;
  }

  @override
  void dispose() {
    _tokenController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final view = _view;
    return Scaffold(
      appBar: AppBar(title: const Text('Safety Card')),
      body: ResponsivePage(
        children: [
          const SafetyNoticeCard(
            title: 'Limited public view',
            message:
                'Safety Cards show only the public booking details needed for a safety check.',
          ),
          const SizedBox(height: 16),
          if (view == null) ...[
            TextField(
              key: const ValueKey('safety-card-token-field'),
              controller: _tokenController,
              enabled: !_isLoading,
              autocorrect: false,
              enableSuggestions: false,
              textInputAction: TextInputAction.done,
              decoration: const InputDecoration(
                labelText: 'Safety Card token',
                hintText: 'Enter the shared token',
                prefixIcon: Icon(Icons.key_outlined),
              ),
              onSubmitted: (_) => _loadCard(),
            ),
            if (_error != null) ...[
              const SizedBox(height: 12),
              OperationStateView(
                icon: Icons.error_outline,
                title: 'Safety Card unavailable',
                message: _error!,
                isError: true,
              ),
            ],
            const SizedBox(height: 16),
            PrimaryActionButton(
              label: _isLoading ? 'Loading...' : 'Open Safety Card',
              icon: Icons.health_and_safety_outlined,
              onPressed: _isLoading ? null : _loadCard,
            ),
          ] else ...[
            _SafetyCardHeader(view: view),
            const SizedBox(height: 16),
            ..._rows(view).map(
              (row) => Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: Semantics(
                  label: '${row.label}: ${row.value}',
                  readOnly: true,
                  child: ExcludeSemantics(
                    child: Card(
                      child: ListTile(
                        title: Text(row.label),
                        subtitle: Text(row.value),
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 8),
            OutlinedButton.icon(
              onPressed: () => setState(() {
                _view = null;
                _error = null;
                _tokenController.clear();
              }),
              icon: const Icon(Icons.search),
              label: const Text('Open another card'),
            ),
          ],
        ],
      ),
    );
  }

  Future<void> _loadCard() async {
    final token = _tokenController.text.trim();
    if (!_tokenPattern.hasMatch(token)) {
      setState(() {
        _error = 'Enter a valid Safety Card token.';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final view = await widget.gateway.getSafetyCard(token);
      if (!mounted) {
        return;
      }
      setState(() => _view = view);
    } catch (error) {
      if (!mounted) {
        return;
      }
      setState(() {
        _error = error is ApiException && error.statusCode == 404
            ? 'This Safety Card was not found or is no longer available.'
            : 'The Safety Card could not be loaded. Check the connection and retry.';
      });
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  List<_SafetyCardRow> _rows(SafetyCardView view) {
    return [
      _SafetyCardRow('Safety Card reference', view.safetyCardReference),
      _SafetyCardRow('Category', _labelFromApiValue(view.category)),
      _SafetyCardRow(
        'Meeting spot',
        '${view.meetingSpot.name}, ${view.meetingSpot.address}',
      ),
      _SafetyCardRow(
        'Start / End',
        '${_formatDateTime(view.startTime)} - ${_formatTime(view.endTime)}',
      ),
      _SafetyCardRow('Companion', view.companionDisplayName),
      _SafetyCardRow('Customer', view.customerDisplayName),
      _SafetyCardRow(
        'Verification summary',
        view.verificationSummary.companionIdentityVerified &&
                view.verificationSummary.companionLivenessVerified
            ? 'Identity and liveness verified'
            : 'Verification incomplete',
      ),
      _SafetyCardRow('Instructions', view.emergencyInstructions),
    ];
  }

  String _labelFromApiValue(String value) {
    return value
        .toLowerCase()
        .split('_')
        .map((part) => '${part[0].toUpperCase()}${part.substring(1)}')
        .join(' ');
  }

  String _formatDateTime(DateTime value) {
    final local = value.toLocal();
    return '${local.day.toString().padLeft(2, '0')}.${local.month.toString().padLeft(2, '0')}.${local.year} ${_formatTime(local)}';
  }

  String _formatTime(DateTime value) {
    final local = value.toLocal();
    return '${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}

class _SafetyCardHeader extends StatelessWidget {
  const _SafetyCardHeader({required this.view});

  final SafetyCardView view;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.primaryContainer,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(
            Icons.verified_user_outlined,
            size: 32,
            color: Theme.of(context).colorScheme.primary,
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Active Safety Card',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 4),
                Text(view.safetyCardReference),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SafetyCardRow {
  const _SafetyCardRow(this.label, this.value);

  final String label;
  final String value;
}
