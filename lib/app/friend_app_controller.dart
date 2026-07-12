import 'package:flutter/foundation.dart';

import '../core/network/api_exception.dart';
import '../features/booking/models/booking_category.dart';
import '../features/booking/models/booking_options.dart';
import '../features/booking/models/booking_request.dart';
import '../features/booking/models/booking_response.dart';
import '../features/booking/services/booking_api.dart';
import '../features/report/models/report_reason.dart';
import '../features/report/models/report_request.dart';
import '../features/report/services/report_api.dart';

class FriendAppController extends ChangeNotifier {
  FriendAppController({
    required this.bookingGateway,
    required this.reportGateway,
    this.currentUserId,
    DateTime Function()? now,
    BookingResponse? initialBooking,
  }) : _now = now ?? DateTime.now,
       _activeBooking = initialBooking {
    final current = _now().toLocal();
    startTime = DateTime(current.year, current.month, current.day + 1, 10);
  }

  final BookingGateway bookingGateway;
  final ReportGateway reportGateway;
  final String? currentUserId;
  final DateTime Function() _now;

  BookingOptions? options;
  BookingCategory? selectedCategory;
  CompanionOption? selectedCompanion;
  MeetingSpotOption? selectedMeetingSpot;
  late DateTime startTime;
  int durationMinutes = 60;

  bool isLoadingOptions = false;
  bool isLoadingLatestBooking = false;
  bool isSubmittingBooking = false;
  bool isPerformingAction = false;
  bool reportSubmitted = false;
  String? optionsError;
  String? latestBookingError;
  String? bookingError;
  String? actionError;

  BookingResponse? _activeBooking;
  bool _disposed = false;
  bool _latestBookingLoaded = false;

  BookingResponse? get activeBooking => _activeBooking;

  DateTime get endTime => startTime.add(Duration(minutes: durationMinutes));

  bool get hasCompleteDraft =>
      selectedCategory != null &&
      selectedCompanion != null &&
      selectedMeetingSpot != null &&
      startTime.isAfter(_now()) &&
      durationMinutes > 0 &&
      durationMinutes <= 120;

  Future<void> loadBookingOptions({bool force = false}) async {
    if (isLoadingOptions || (!force && options != null)) {
      return;
    }
    isLoadingOptions = true;
    optionsError = null;
    _notify();

    try {
      final loaded = await bookingGateway.getOptions();
      options = loaded;
      selectedCompanion = _matchingCompanion(
        loaded.companions,
        selectedCompanion?.id,
      );
      selectedMeetingSpot = _matchingMeetingSpot(
        loaded.meetingSpots,
        selectedMeetingSpot?.id,
      );
    } catch (error) {
      optionsError = _userMessage(error, operation: 'load booking options');
    } finally {
      isLoadingOptions = false;
      _notify();
    }
  }

  Future<void> loadLatestBooking({bool force = false}) async {
    if (isLoadingLatestBooking ||
        (!force && (_latestBookingLoaded || _activeBooking != null))) {
      return;
    }
    isLoadingLatestBooking = true;
    latestBookingError = null;
    _notify();
    try {
      _activeBooking = await bookingGateway.getLatestBooking();
      _latestBookingLoaded = true;
    } catch (error) {
      latestBookingError = _userMessage(
        error,
        operation: 'load your latest booking',
      );
    } finally {
      isLoadingLatestBooking = false;
      _notify();
    }
  }

  void selectCategory(BookingCategory category) {
    selectedCategory = category;
    bookingError = null;
    _notify();
  }

  void selectCompanion(CompanionOption companion) {
    selectedCompanion = companion;
    bookingError = null;
    _notify();
  }

  void selectMeetingSpot(MeetingSpotOption meetingSpot) {
    selectedMeetingSpot = meetingSpot;
    bookingError = null;
    _notify();
  }

  void setSchedule({required DateTime start, required int duration}) {
    startTime = start;
    durationMinutes = duration;
    bookingError = null;
    _notify();
  }

  void clearActionFeedback() {
    actionError = null;
    reportSubmitted = false;
    _notify();
  }

  Future<bool> submitBooking() async {
    if (isSubmittingBooking) {
      return false;
    }
    if (!hasCompleteDraft) {
      bookingError = 'Complete every booking detail before submitting.';
      _notify();
      return false;
    }

    isSubmittingBooking = true;
    bookingError = null;
    _notify();

    try {
      _activeBooking = await bookingGateway.createBooking(
        BookingRequest(
          companionId: selectedCompanion!.id,
          category: selectedCategory!,
          meetingSpotId: selectedMeetingSpot!.id,
          startTime: startTime,
          endTime: endTime,
        ),
      );
      _latestBookingLoaded = true;
      return true;
    } catch (error) {
      bookingError = _userMessage(error, operation: 'submit this booking');
      return false;
    } finally {
      isSubmittingBooking = false;
      _notify();
    }
  }

  Future<bool> checkIn() {
    return _performBookingAction(
      'check in',
      (booking) =>
          bookingGateway.checkIn(booking.id, const CheckInOutRequest()),
    );
  }

  Future<bool> checkOut() {
    return _performBookingAction(
      'check out',
      (booking) =>
          bookingGateway.checkOut(booking.id, const CheckInOutRequest()),
    );
  }

  Future<bool> submitReport({
    required ReportReason reason,
    required bool blockReportedUser,
  }) async {
    final booking = _activeBooking;
    if (booking == null || isPerformingAction) {
      return false;
    }

    final reportedUserId = _reportedUserId(booking);
    if (reportedUserId == null) {
      actionError = 'Your account does not match either booking participant.';
      _notify();
      return false;
    }

    isPerformingAction = true;
    actionError = null;
    reportSubmitted = false;
    _notify();
    try {
      await reportGateway.reportBooking(
        booking.id,
        ReportRequest(
          reportedUserId: reportedUserId,
          reason: reason,
          blockReportedUser: blockReportedUser,
        ),
      );
      reportSubmitted = true;
      return true;
    } catch (error) {
      actionError = _userMessage(error, operation: 'submit this report');
      return false;
    } finally {
      isPerformingAction = false;
      _notify();
    }
  }

  String? _reportedUserId(BookingResponse booking) {
    if (currentUserId == null || currentUserId == booking.customerId) {
      return booking.companionId;
    }
    if (currentUserId == booking.companionId) {
      return booking.customerId;
    }
    return null;
  }

  Future<bool> _performBookingAction(
    String operation,
    Future<BookingResponse> Function(BookingResponse booking) action,
  ) async {
    final booking = _activeBooking;
    if (booking == null || isPerformingAction) {
      return false;
    }
    isPerformingAction = true;
    actionError = null;
    _notify();
    try {
      _activeBooking = await action(booking);
      return true;
    } catch (error) {
      actionError = _userMessage(error, operation: operation);
      return false;
    } finally {
      isPerformingAction = false;
      _notify();
    }
  }

  CompanionOption? _matchingCompanion(
    List<CompanionOption> companions,
    String? id,
  ) {
    if (id == null) {
      return null;
    }
    for (final companion in companions) {
      if (companion.id == id) {
        return companion;
      }
    }
    return null;
  }

  MeetingSpotOption? _matchingMeetingSpot(
    List<MeetingSpotOption> meetingSpots,
    String? id,
  ) {
    if (id == null) {
      return null;
    }
    for (final meetingSpot in meetingSpots) {
      if (meetingSpot.id == id) {
        return meetingSpot;
      }
    }
    return null;
  }

  String _userMessage(Object error, {required String operation}) {
    if (error is ApiException) {
      return switch (error.statusCode) {
        0 =>
          'The server could not be reached. Check your connection and retry.',
        400 => 'Some details were not accepted. Review them and try again.',
        401 || 403 => 'Your session is not authorized for this action.',
        404 => 'The requested booking information is no longer available.',
        409 => 'The booking state changed. Refresh before trying again.',
        _ => 'The server could not $operation. Please retry.',
      };
    }
    return 'Something went wrong while trying to $operation. Please retry.';
  }

  void _notify() {
    if (!_disposed) {
      notifyListeners();
    }
  }

  @override
  void dispose() {
    _disposed = true;
    super.dispose();
  }
}
