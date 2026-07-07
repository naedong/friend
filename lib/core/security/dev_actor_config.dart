class DevActorConfig {
  DevActorConfig({
    required this.isProduction,
    required this.enabled,
    this.actorId,
  }) {
    if (isProduction && (enabled || actorId != null)) {
      throw StateError('Production builds must not configure X-Dev-Actor-Id.');
    }
  }

  factory DevActorConfig.fromDartDefines({required bool isProduction}) {
    const enabled = bool.fromEnvironment('FRIEND_DEV_ACTOR_ENABLED');
    const actorId = String.fromEnvironment('FRIEND_DEV_ACTOR_ID');

    return DevActorConfig(
      isProduction: isProduction,
      enabled: enabled,
      actorId: actorId.isEmpty ? null : actorId,
    );
  }

  final bool isProduction;
  final bool enabled;
  final String? actorId;

  Map<String, String> get headers {
    if (isProduction || !enabled || actorId == null) {
      return const {};
    }

    // TODO AUTH: Remove this temporary development shortcut when real auth ships.
    // Never send this header in production builds.
    return {'X-Dev-Actor-Id': actorId!};
  }
}
