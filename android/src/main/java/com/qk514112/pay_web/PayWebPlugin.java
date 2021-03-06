package com.qk514112.pay_web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * PayWebPlugin
 */
public class PayWebPlugin implements FlutterPlugin,
        MethodCallHandler,
        ActivityAware,
        PluginRegistry.RequestPermissionsResultListener,
        PluginRegistry.ActivityResultListener {
    /// 打印日志时用到
    final String TAG = "PayWebPlugin";
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    /// 插件关联的 activity
    private Activity activity;
    /// 插件关联的上下文
    private Context context;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "pay_web");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("openWebPayView")) {
            // 解析从 flutter 传过来的参数
            String url = call.argument("url");
            String title = call.argument("title");
            String postValue = call.argument("postValue");

            // 打开网页
            Intent intent = new Intent(context, WebPayViewActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            intent.putExtra("postValue", postValue);
            activity.startActivity(intent);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        context = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to an Activity
        activity = activityPluginBinding.getActivity();
        activityPluginBinding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // TODO: the Activity your plugin was attached to was
        // destroyed to change configuration.
        // This call will be followed by onReattachedToActivityForConfigChanges().
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to a new Activity
        // after a configuration change.
        activity = activityPluginBinding.getActivity();
        activityPluginBinding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        // TODO: your plugin is no longer associated with an Activity.
        // Clean up references.
        activity = null;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: 测试代码
        // 测试有返回结果的 activity 跳转
//        if (requestCode == REQUEST_CODE_OPEN && resultCode == RESULT_OK) {
//            Toast.makeText(activity, "adfadfadfasdfas", Toast.LENGTH_LONG).show();
//            return  true;
//        }
//        Toast.makeText(activity, "" + resultCode, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onActivityResult" + resultCode);
        return false;
    }
}
