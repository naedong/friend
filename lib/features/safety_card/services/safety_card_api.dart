import '../../../core/network/api_client.dart';
import '../models/safety_card_view.dart';

class SafetyCardApi {
  const SafetyCardApi(this._apiClient);

  final ApiClient _apiClient;

  Future<SafetyCardView> getSafetyCard(String token) async {
    final encodedToken = Uri.encodeComponent(token);
    final json = await _apiClient.get('/api/safety-cards/$encodedToken');
    return SafetyCardView.fromJson(json);
  }
}
