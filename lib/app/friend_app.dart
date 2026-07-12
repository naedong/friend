import 'package:flutter/material.dart';

import '../core/config/app_environment.dart';
import '../core/network/api_client.dart';
import '../features/booking/screens/booking_request_screen.dart';
import '../features/booking/screens/booking_status_screen.dart';
import '../features/booking/screens/category_selection_screen.dart';
import '../features/booking/screens/check_in_out_screen.dart';
import '../features/booking/screens/companion_selection_screen.dart';
import '../features/booking/screens/safe_meeting_spot_screen.dart';
import '../features/booking/services/booking_api.dart';
import '../features/home/home_screen.dart';
import '../features/onboarding/product_boundary_screen.dart';
import '../features/profile/screens/profile_screen.dart';
import '../features/report/screens/report_screen.dart';
import '../features/report/services/report_api.dart';
import '../features/safety_card/screens/safety_card_screen.dart';
import '../features/safety_card/services/safety_card_api.dart';
import 'app_routes.dart';
import 'app_theme.dart';
import 'friend_app_controller.dart';

class FriendApp extends StatefulWidget {
  const FriendApp({
    required this.environment,
    this.controller,
    this.safetyCardGateway,
    super.key,
  });

  final AppEnvironment environment;
  final FriendAppController? controller;
  final SafetyCardGateway? safetyCardGateway;

  @override
  State<FriendApp> createState() => _FriendAppState();
}

class _FriendAppState extends State<FriendApp> {
  ApiClient? _apiClient;
  late final FriendAppController _controller;
  late final SafetyCardGateway _safetyCardGateway;
  late final bool _ownsController;

  @override
  void initState() {
    super.initState();
    _ownsController = widget.controller == null;
    if (_ownsController || widget.safetyCardGateway == null) {
      _apiClient = ApiClient(environment: widget.environment);
    }
    _controller =
        widget.controller ??
        FriendAppController(
          bookingGateway: BookingApi(_apiClient!),
          reportGateway: ReportApi(_apiClient!),
          currentUserId: widget.environment.devActorConfig.actorId,
        );
    _safetyCardGateway = widget.safetyCardGateway ?? SafetyCardApi(_apiClient!);
  }

  @override
  void dispose() {
    if (_ownsController) {
      _controller.dispose();
    }
    _apiClient?.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Friend',
      debugShowCheckedModeBanner: false,
      theme: buildAppTheme(),
      initialRoute: AppRoutes.productBoundary,
      routes: {
        AppRoutes.productBoundary: (_) => const ProductBoundaryScreen(),
        AppRoutes.home: (_) => HomeScreen(
          environment: widget.environment,
          controller: _controller,
        ),
        AppRoutes.categorySelection: (_) =>
            CategorySelectionScreen(controller: _controller),
        AppRoutes.companionSelection: (_) =>
            CompanionSelectionScreen(controller: _controller),
        AppRoutes.safeMeetingSpot: (_) =>
            SafeMeetingSpotScreen(controller: _controller),
        AppRoutes.bookingRequest: (_) =>
            BookingRequestScreen(controller: _controller),
        AppRoutes.bookingStatus: (_) =>
            BookingStatusScreen(controller: _controller),
        AppRoutes.checkInOut: (_) => CheckInOutScreen(controller: _controller),
        AppRoutes.safetyCard: (_) =>
            SafetyCardScreen(gateway: _safetyCardGateway),
        AppRoutes.report: (_) => ReportScreen(controller: _controller),
        AppRoutes.profile: (_) => ProfileScreen(
          environment: widget.environment,
          controller: _controller,
        ),
      },
    );
  }
}
