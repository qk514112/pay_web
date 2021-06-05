import 'dart:async';

import 'package:flutter/services.dart';

class PayWeb {
  static const MethodChannel _channel = const MethodChannel('pay_web');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// 打开支付网页
  static Future<void> get openWebPayView async {
    await _channel.invokeMethod('openWebPayView');
  }
}
