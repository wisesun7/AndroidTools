package com.wise.sun.androidtools.SystemUtils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by wise on 2019/6/25.
 * {@link #get 获取系统属性}
 * {@link #set 设置系统属性}
 */

public class SystemUtils {
    private static final String TAG = "SystemUtils";
    private static volatile Method set = null;
    private static volatile Method get = null;

    public static void set(String prop, String value) {
        try {
            if (null == set) {
                synchronized (SystemUtils.class) {
                    if (null == set) {
                        Class<?> cls = Class.forName("android.os.SystemProperties");
                        set = cls.getDeclaredMethod("set", new Class<?>[]{String.class, String.class});
                    }
                }
            }
            set.invoke(null, new Object[]{prop, value});
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String get(String prop, String defaultvalue) {
        String value = defaultvalue;
        try {
            if (null == get) {
                synchronized (SystemUtils.class) {
                    if (null == get) {
                        Class<?> cls = Class.forName("android.os.SystemProperties");
                        get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                    }
                }
            }
            value = (String) (get.invoke(null, new Object[]{prop, defaultvalue}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取当前可用空间
     * @return
     */
    public static float getAvailableSpace() {
        File file = Environment.getDataDirectory();
        StatFs sf = new StatFs(file.getPath());
        long blocks = sf.getAvailableBlocksLong();
        long blockSpace = sf.getBlockSizeLong();
        float space = (blocks * blockSpace * 1.0f) / (1024 * 1024 * 1024);
        Log.d(TAG, "getAvailableSpace: " + Math.round(space * 100) / 100);
        return (float) Math.round(space * 100) / 100;
    }

    /**
     * 获取系统占用空间
     * @return
     */
    public static float getSystemSpace() {
        File file = Environment.getRootDirectory();
        StatFs sf = new StatFs(file.getPath());
        long blocks = sf.getAvailableBlocksLong();
        long blockSpace = sf.getBlockSizeLong();
        float space = (blocks * blockSpace * 1.0f) / (1024 * 1024 * 1024);
        Log.d(TAG, "getAvailableSpace: " + Math.round(space * 100) / 100);
        return (float) Math.round(space * 100) / 100;
    }


    /**
     * 获取总空间（不包含系统占用）
     * @param context
     * @return
     */
    public static long getRomTotalSize(Context context) {
        File rom = Environment.getDataDirectory();
        StatFs statFs = new StatFs(rom.getPath());
        long totalSpace = statFs.getTotalBytes();
        return totalSpace;
    }
}
