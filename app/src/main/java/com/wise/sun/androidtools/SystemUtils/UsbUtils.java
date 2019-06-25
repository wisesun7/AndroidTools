package com.wise.sun.androidtools.SystemUtils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wise on 2019/6/25.
 * {@link #getUsbPaths(Context)}
 * {@link #getValidUsbPaths(Context)}
 * {@link #getVolumeState(Context, String)}
 */

public class UsbUtils {

    private static final String TAG = "UsbUtil";
    private String mountState;
    private static List<String> getUsbPaths(Context context) {
        StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method method = manager.getClass().getMethod("getVolumePaths");
            String[] paths = (String[]) method.invoke(manager);
            if (paths != null && paths.length != 0) {
                return Arrays.asList(paths);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean getVolumeState(Context context,String volumePoint){
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method method = sm.getClass().getMethod("getVolumeState",String.class);
            String state = (String) method.invoke(sm,volumePoint);
            Log.d(TAG,"volume state is :" + state);
            if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_UNKNOWN)){
                return true;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getValidUsbPaths(Context context) {
        List<String> usbpaths = new ArrayList<String>();
        List<String> result = getUsbPaths(context);
        if (result != null && result.size() > 0) {
            for (String path : result) {
                String lowPath = path.toLowerCase();
                Log.d(TAG,"path is : " + lowPath);
                if (lowPath.contains("emulated") || lowPath.contains("null") || !new File(path).exists() || !new File(path).canRead() || !getVolumeState(context,lowPath)) {
                    continue;
                }
                usbpaths.add(path);
            }
        }
        return usbpaths;
    }

}
