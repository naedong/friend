enum ReportReason {
  harassment('HARASSMENT', 'Harassment'),
  unsafeBehavior('UNSAFE_BEHAVIOR', 'Unsafe behavior'),
  offPlatformPayment('OFF_PLATFORM_PAYMENT', 'Off-platform payment'),
  privateMeetingRequest('PRIVATE_MEETING_REQUEST', 'Private meeting request'),
  sexualOrSuggestiveBehavior(
    'SEXUAL_OR_SUGGESTIVE_BEHAVIOR',
    'Sexual or suggestive behavior',
  ),
  fakeIdentity('FAKE_IDENTITY', 'Fake identity'),
  noShow('NO_SHOW', 'No show'),
  other('OTHER', 'Other');

  const ReportReason(this.apiValue, this.label);

  final String apiValue;
  final String label;
}
