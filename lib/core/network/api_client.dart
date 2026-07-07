import 'dart:convert';
import 'dart:io';

import '../config/app_environment.dart';
import 'api_exception.dart';

class ApiClient {
  ApiClient({required this.environment, HttpClient? httpClient})
    : _httpClient = httpClient ?? HttpClient();

  final AppEnvironment environment;
  final HttpClient _httpClient;

  Future<Map<String, Object?>> get(String path) {
    return _send(method: 'GET', path: path);
  }

  Future<Map<String, Object?>> postJson(
    String path, {
    Map<String, Object?>? body,
  }) {
    return _send(method: 'POST', path: path, body: body);
  }

  Future<Map<String, Object?>> _send({
    required String method,
    required String path,
    Map<String, Object?>? body,
  }) async {
    final uri = environment.apiBaseUrl.resolve(path);
    if (environment.isProduction && uri.scheme != 'https') {
      throw StateError('Production API requests must use HTTPS.');
    }

    final request = await _httpClient.openUrl(method, uri);
    request.headers.contentType = ContentType.json;
    request.headers.set(HttpHeaders.acceptHeader, ContentType.json.mimeType);
    for (final header in environment.requestHeaders().entries) {
      request.headers.set(header.key, header.value);
    }

    if (body != null) {
      request.write(jsonEncode(body));
    }

    // TODO SECURITY: Add certificate pinning after production domains are stable.
    final response = await request.close();
    final responseBody = await utf8.decodeStream(response);

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw ApiException(
        statusCode: response.statusCode,
        message: 'Backend request failed',
        responseBody: responseBody,
      );
    }

    if (responseBody.isEmpty) {
      return <String, Object?>{};
    }

    final decoded = jsonDecode(responseBody);
    if (decoded is! Map<String, Object?>) {
      throw ApiException(
        statusCode: response.statusCode,
        message: 'Unexpected backend response shape',
        responseBody: responseBody,
      );
    }
    return decoded;
  }

  void close() {
    _httpClient.close(force: true);
  }
}
