class SafetyCardView {
  const SafetyCardView({
    required this.safetyCardReference,
    required this.category,
    required this.meetingSpot,
    required this.startTime,
    required this.endTime,
    required this.companionDisplayName,
    required this.customerDisplayName,
    required this.verificationSummary,
    required this.emergencyInstructions,
  });

  final String safetyCardReference;
  final String category;
  final SafetyCardMeetingSpot meetingSpot;
  final DateTime startTime;
  final DateTime endTime;
  final String companionDisplayName;
  final String customerDisplayName;
  final VerificationSummary verificationSummary;
  final String emergencyInstructions;

  factory SafetyCardView.fromJson(Map<String, Object?> json) {
    return SafetyCardView(
      safetyCardReference: json['safetyCardReference'] as String,
      category: json['category'] as String,
      meetingSpot: SafetyCardMeetingSpot.fromJson(
        json['meetingSpot'] as Map<String, Object?>,
      ),
      startTime: DateTime.parse(json['startTime'] as String),
      endTime: DateTime.parse(json['endTime'] as String),
      companionDisplayName: json['companionDisplayName'] as String,
      customerDisplayName: json['customerDisplayName'] as String,
      verificationSummary: VerificationSummary.fromJson(
        json['verificationSummary'] as Map<String, Object?>,
      ),
      emergencyInstructions: json['emergencyInstructions'] as String,
    );
  }

  Map<String, Object?> toDisplayJson() {
    return {
      'safetyCardReference': safetyCardReference,
      'category': category,
      'meetingSpot': meetingSpot.toDisplayJson(),
      'startTime': startTime.toUtc().toIso8601String(),
      'endTime': endTime.toUtc().toIso8601String(),
      'companionDisplayName': companionDisplayName,
      'customerDisplayName': customerDisplayName,
      'verificationSummary': verificationSummary.toDisplayJson(),
      'emergencyInstructions': emergencyInstructions,
    };
  }
}

class SafetyCardMeetingSpot {
  const SafetyCardMeetingSpot({required this.name, required this.address});

  final String name;
  final String address;

  factory SafetyCardMeetingSpot.fromJson(Map<String, Object?> json) {
    return SafetyCardMeetingSpot(
      name: json['name'] as String,
      address: json['address'] as String,
    );
  }

  Map<String, Object?> toDisplayJson() {
    return {'name': name, 'address': address};
  }
}

class VerificationSummary {
  const VerificationSummary({
    required this.companionIdentityVerified,
    required this.companionLivenessVerified,
  });

  final bool companionIdentityVerified;
  final bool companionLivenessVerified;

  factory VerificationSummary.fromJson(Map<String, Object?> json) {
    return VerificationSummary(
      companionIdentityVerified: json['companionIdentityVerified'] as bool,
      companionLivenessVerified: json['companionLivenessVerified'] as bool,
    );
  }

  Map<String, Object?> toDisplayJson() {
    return {
      'companionIdentityVerified': companionIdentityVerified,
      'companionLivenessVerified': companionLivenessVerified,
    };
  }
}
