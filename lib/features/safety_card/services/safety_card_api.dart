import '../../../core/network/api_client.dart';
import '../models/safety_card_view.dart';

abstract interface class SafetyCardGateway {
  Future<SafetyCardView> getSafetyCard(String token);
}

class SafetyCardApi implements SafetyCardGateway {
  const SafetyCardApi(this._apiClient);

  final ApiClient _apiClient;

  @override
  Future<SafetyCardView> getSafetyCard(String token) async {
    final encodedToken = Uri.encodeComponent(token);
    final json = await _apiClient.get('/api/safety-cards/$encodedToken');
    return SafetyCardView.fromJson(json);
  }
}
