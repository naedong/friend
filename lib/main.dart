import 'package:flutter/material.dart';

import 'app/friend_app.dart';
import 'core/config/app_environment.dart';

void main() {
  runApp(FriendApp(environment: AppEnvironment.fromDartDefines()));
}
