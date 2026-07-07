class ApiException implements Exception {
  ApiException({
    required this.statusCode,
    required this.message,
    this.responseBody,
  });

  final int statusCode;
  final String message;
  final String? responseBody;

  @override
  String toString() => 'ApiException($statusCode): $message';
}
