import 'package:flutter/material.dart';

ThemeData buildAppTheme() {
  const colorScheme = ColorScheme.light(
    primary: Color(0xFF006A67),
    onPrimary: Colors.white,
    primaryContainer: Color(0xFFC7F1ED),
    onPrimaryContainer: Color(0xFF003735),
    secondary: Color(0xFF9E4032),
    onSecondary: Colors.white,
    secondaryContainer: Color(0xFFFFDAD3),
    onSecondaryContainer: Color(0xFF3E0500),
    tertiary: Color(0xFF6B5D00),
    onTertiary: Colors.white,
    tertiaryContainer: Color(0xFFF5E47A),
    onTertiaryContainer: Color(0xFF211B00),
    surface: Color(0xFFFBFDFC),
    onSurface: Color(0xFF17201F),
    surfaceContainerHighest: Color(0xFFE2EAE8),
    onSurfaceVariant: Color(0xFF3F4947),
    outline: Color(0xFF6F7977),
    outlineVariant: Color(0xFFBEC9C7),
    error: Color(0xFFBA1A1A),
    onError: Colors.white,
    errorContainer: Color(0xFFFFDAD6),
    onErrorContainer: Color(0xFF410002),
  );

  return ThemeData(
    colorScheme: colorScheme,
    useMaterial3: true,
    scaffoldBackgroundColor: const Color(0xFFF4F7F6),
    appBarTheme: const AppBarTheme(
      centerTitle: false,
      backgroundColor: Color(0xFFF4F7F6),
      foregroundColor: Color(0xFF17201F),
      surfaceTintColor: Colors.transparent,
      elevation: 0,
    ),
    cardTheme: const CardThemeData(
      color: Colors.white,
      elevation: 0,
      margin: EdgeInsets.zero,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(8)),
        side: BorderSide(color: Color(0xFFBEC9C7)),
      ),
    ),
    listTileTheme: const ListTileThemeData(
      iconColor: Color(0xFF126A72),
      contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    ),
    filledButtonTheme: FilledButtonThemeData(
      style: FilledButton.styleFrom(
        minimumSize: const Size(48, 48),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        textStyle: const TextStyle(fontWeight: FontWeight.w700),
      ),
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        minimumSize: const Size(48, 48),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        textStyle: const TextStyle(fontWeight: FontWeight.w700),
      ),
    ),
    inputDecorationTheme: const InputDecorationTheme(
      filled: true,
      fillColor: Colors.white,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.all(Radius.circular(8)),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.all(Radius.circular(8)),
        borderSide: BorderSide(color: Color(0xFFBEC9C7)),
      ),
      contentPadding: EdgeInsets.symmetric(horizontal: 14, vertical: 14),
    ),
    snackBarTheme: const SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      showCloseIcon: true,
    ),
    progressIndicatorTheme: const ProgressIndicatorThemeData(
      linearTrackColor: Color(0xFFD9E4E2),
    ),
  );
}
