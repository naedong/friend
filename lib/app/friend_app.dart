import 'package:flutter/material.dart';

import '../core/config/app_environment.dart';
import '../features/booking/screens/booking_request_screen.dart';
import '../features/booking/screens/booking_status_screen.dart';
import '../features/booking/screens/category_selection_screen.dart';
import '../features/booking/screens/check_in_out_screen.dart';
import '../features/booking/screens/safe_meeting_spot_screen.dart';
import '../features/home/home_screen.dart';
import '../features/onboarding/product_boundary_screen.dart';
import '../features/profile/screens/profile_placeholder_screen.dart';
import '../features/report/screens/report_screen.dart';
import '../features/safety_card/screens/safety_card_screen.dart';
import 'app_routes.dart';
import 'app_theme.dart';

class FriendApp extends StatelessWidget {
  const FriendApp({required this.environment, super.key});

  final AppEnvironment environment;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Friend',
      theme: buildAppTheme(),
      initialRoute: AppRoutes.productBoundary,
      routes: {
        AppRoutes.productBoundary: (_) => const ProductBoundaryScreen(),
        AppRoutes.home: (_) => HomeScreen(environment: environment),
        AppRoutes.categorySelection: (_) => const CategorySelectionScreen(),
        AppRoutes.safeMeetingSpot: (_) => const SafeMeetingSpotScreen(),
        AppRoutes.bookingRequest: (_) => const BookingRequestScreen(),
        AppRoutes.bookingStatus: (_) => const BookingStatusScreen(),
        AppRoutes.checkInOut: (_) => const CheckInOutScreen(),
        AppRoutes.safetyCard: (_) => const SafetyCardScreen(),
        AppRoutes.report: (_) => const ReportScreen(),
        AppRoutes.profile: (_) => const ProfilePlaceholderScreen(),
      },
    );
  }
}
