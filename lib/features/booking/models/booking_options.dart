class BookingOptions {
  const BookingOptions({required this.companions, required this.meetingSpots});

  final List<CompanionOption> companions;
  final List<MeetingSpotOption> meetingSpots;

  factory BookingOptions.fromJson(Map<String, Object?> json) {
    return BookingOptions(
      companions: _jsonList(
        json,
        'companions',
      ).map(CompanionOption.fromJson).toList(growable: false),
      meetingSpots: _jsonList(
        json,
        'meetingSpots',
      ).map(MeetingSpotOption.fromJson).toList(growable: false),
    );
  }

  static Iterable<Map<String, Object?>> _jsonList(
    Map<String, Object?> json,
    String key,
  ) {
    final value = json[key];
    if (value is! List<Object?>) {
      throw FormatException('Expected $key to be a list.');
    }
    return value.map((item) {
      if (item is! Map<String, Object?>) {
        throw FormatException('Expected $key entries to be objects.');
      }
      return item;
    });
  }
}

class CompanionOption {
  const CompanionOption({
    required this.id,
    required this.displayName,
    required this.bio,
    required this.identityVerified,
    required this.livenessVerified,
  });

  final String id;
  final String displayName;
  final String bio;
  final bool identityVerified;
  final bool livenessVerified;

  bool get fullyVerified => identityVerified && livenessVerified;

  factory CompanionOption.fromJson(Map<String, Object?> json) {
    return CompanionOption(
      id: json['id'] as String,
      displayName: json['displayName'] as String,
      bio: (json['bio'] as String?) ?? '',
      identityVerified: json['identityVerified'] as bool,
      livenessVerified: json['livenessVerified'] as bool,
    );
  }
}

class MeetingSpotOption {
  const MeetingSpotOption({
    required this.id,
    required this.name,
    required this.address,
    required this.type,
  });

  final String id;
  final String name;
  final String address;
  final String type;

  String get typeLabel {
    return type
        .toLowerCase()
        .split('_')
        .map((part) => '${part[0].toUpperCase()}${part.substring(1)}')
        .join(' ');
  }

  factory MeetingSpotOption.fromJson(Map<String, Object?> json) {
    return MeetingSpotOption(
      id: json['id'] as String,
      name: json['name'] as String,
      address: json['address'] as String,
      type: json['type'] as String,
    );
  }
}
