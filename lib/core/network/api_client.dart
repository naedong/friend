import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;

import '../config/app_environment.dart';
import 'api_exception.dart';

class ApiClient {
  ApiClient({
    required this.environment,
    http.Client? httpClient,
    this.requestTimeout = const Duration(seconds: 15),
  }) : _httpClient = httpClient ?? http.Client();

  final AppEnvironment environment;
  final http.Client _httpClient;
  final Duration requestTimeout;

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

    final request = http.Request(method, uri)
      ..headers.addAll({
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        ...environment.requestHeaders(),
      });

    if (body != null) {
      request.body = jsonEncode(body);
    }

    // TODO SECURITY: Add certificate pinning after production domains are stable.
    late http.Response response;
    try {
      final streamedResponse = await _httpClient
          .send(request)
          .timeout(requestTimeout);
      response = await http.Response.fromStream(streamedResponse);
    } on TimeoutException {
      throw ApiException(statusCode: 0, message: 'Backend request timed out');
    } on http.ClientException {
      throw ApiException(statusCode: 0, message: 'Backend is unreachable');
    }

    final responseBody = utf8.decode(response.bodyBytes);

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
    _httpClient.close();
  }
}
