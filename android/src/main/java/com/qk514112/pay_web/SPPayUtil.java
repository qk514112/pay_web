package com.qk514112.pay_web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPPayUtil {
    private static final Map<String, String> imageExtMap;
    static
    {
        imageExtMap = new HashMap<>();
        imageExtMap.put("png", "");
        imageExtMap.put("jpg", "");
        imageExtMap.put("gif", "");
        imageExtMap.put("bmp", "");
        imageExtMap.put("wbmp", "");
        imageExtMap.put("webp", "");
    }

    public static void disableAccessibility(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (!am.isEnabled()) {
                    //Not need to disable accessibility
                    return;
                }
                Method setState = am.getClass().getDeclaredMethod("setState", int.class);
                setState.setAccessible(true);
                setState.invoke(am, 0);/**{@link AccessibilityManager#STATE_FLAG_ACCESSIBILITY_ENABLED}*/
            } catch (Throwable t) {
            }
        }
    }

    public static void startUrl(Context context, String url, boolean isNewTask) {
        if(context != null && !TextUtils.isEmpty(url)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if(isNewTask) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

            } catch (Exception e) {
            }
        }
    }

    public static boolean hasActivity(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo info : appList) {
            if (info.activityInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    public static void startAppMarketWithUrl(Context context, String url, boolean forceUseGoogle) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (forceUseGoogle || hasActivity(context, intent, "com.android.vending"))
                intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                startUrl(context, url, true);
            } catch (Exception e1) {}
        }
    }

    public static boolean isActivityDestroy(Activity activity) {
        if (activity == null)
            return true;

        if (Build.VERSION.SDK_INT >= 17) {
            return activity.isDestroyed();
        }
        return activity.isFinishing();
    }

    public static int checkSelfPermission(Context context, String permission) {
        try {
            return ActivityCompat.checkSelfPermission(context, permission);
        }catch (Exception e){
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public static boolean isImageFile(String fileName) {
        if(TextUtils.isEmpty(fileName))
            return false;
        fileName = fileName.trim();
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex != -1 && lastDotIndex != fileName.length() -1) {
            String fileExt = fileName.substring(lastDotIndex+1).toLowerCase();
            return !TextUtils.isEmpty(fileExt) && imageExtMap.containsKey(fileExt);
        }
        return false;
    }
}
