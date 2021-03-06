import 'dart:async';

import 'package:flutter/services.dart';

class PayWeb {
  static const MethodChannel _channel = const MethodChannel('pay_web');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// 打开支付网页
  /// [url] 网页地址
  /// [title] 网页标题
  /// [postValue] 其他参数
  static Future<void> openWebPayView(String url, String title, String postValue) async {
    final params = {
      'url': url,
      'title': title,
      'postValue': postValue,
    };
    await _channel.invokeMethod('openWebPayView', params);
  }
}
