import '../security/dev_actor_config.dart';

enum EnvironmentKind {
  dev,
  test,
  production;

  bool get allowsDevActor => this == EnvironmentKind.dev;
  bool get isProduction => this == EnvironmentKind.production;

  static EnvironmentKind parse(String value) {
    return switch (value.trim().toLowerCase()) {
      '' || 'dev' || 'development' => EnvironmentKind.dev,
      'test' => EnvironmentKind.test,
      'prod' || 'production' => EnvironmentKind.production,
      _ => throw ArgumentError.value(
        value,
        'FRIEND_ENV',
        'Unsupported environment',
      ),
    };
  }
}

class AppEnvironment {
  AppEnvironment({
    required this.kind,
    required this.apiBaseUrl,
    required this.devActorConfig,
  }) {
    _validate();
  }

  factory AppEnvironment.fromDartDefines() {
    const rawKind = String.fromEnvironment('FRIEND_ENV', defaultValue: 'dev');
    const rawBaseUrl = String.fromEnvironment(
      'FRIEND_API_BASE_URL',
      defaultValue: 'http://10.0.2.2:8080',
    );
    final kind = EnvironmentKind.parse(rawKind);

    return AppEnvironment(
      kind: kind,
      apiBaseUrl: Uri.parse(rawBaseUrl),
      devActorConfig: DevActorConfig.fromDartDefines(
        isProduction: kind.isProduction,
      ),
    );
  }

  final EnvironmentKind kind;
  final Uri apiBaseUrl;
  final DevActorConfig devActorConfig;

  bool get isProduction => kind.isProduction;

  Map<String, String> requestHeaders() {
    if (!kind.allowsDevActor) {
      return const {};
    }

    return {
      ...devActorConfig.headers,
      // TODO AUTH: Add Authorization header after real mobile auth is implemented.
    };
  }

  void _validate() {
    if (!apiBaseUrl.hasScheme || apiBaseUrl.host.isEmpty) {
      throw ArgumentError.value(
        apiBaseUrl,
        'apiBaseUrl',
        'API base URL must be absolute',
      );
    }
    if (isProduction && apiBaseUrl.scheme != 'https') {
      throw StateError('Production API base URL must use HTTPS.');
    }
    if (!kind.allowsDevActor &&
        (devActorConfig.enabled || devActorConfig.actorId != null)) {
      throw StateError('X-Dev-Actor-Id can only be configured for dev builds.');
    }
  }
}
