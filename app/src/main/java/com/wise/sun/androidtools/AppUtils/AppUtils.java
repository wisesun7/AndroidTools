package com.wise.sun.androidtools.AppUtils;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by wise on 2019/6/25.
 * {@link #getVersionCode(Context)}
 * {@link #getVersionName(Context)}
 * {@link #isActivityForeground(Context, String)}
 * {@link #isServiceRunning(Context, String)}
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static String getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            Log.d(TAG, "getVersionCode: versionCode: " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(versionCode);
    }

    public static String getVersionName(Context context) {
        String versionName = null;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.d(TAG, "getVersionName: versionName: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 判断某个Activity界面是否在前台
     */
    public static boolean  isActivityForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断服务是否启动
     */
    public static boolean isServiceRunning(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            String serviceName = serviceList.get(i).service.getClassName();
            // Log.d("kxj", "isServiceRunning: className:" + serviceName);
            if (serviceName.equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
