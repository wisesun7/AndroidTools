package com.wise.sun.androidtools.FileUtils;

import android.util.EventLogTags;
import android.util.Log;

import java.io.File;

/**
 * Created by wise on 2019/6/19.
 * {@link #checkDirExsit(String)}
 * {@link #delAllFile(String)}
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    private void checkDirExsit(String dir) {
        try {
            File file = new File(dir);
            if (!(file.exists() && file.isDirectory())) {
                Log.i(TAG, "path not exist,make dir");
                file.mkdir();
            } else {
                Log.i(TAG, "path already exist");
            }
        } catch (Exception e) {
            Log.e(TAG, " check path error, e:" + e.getMessage());
        }
    }

    public boolean delAllFile(String path) throws Exception {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        if (tempList != null){
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    Log.i(TAG, " delete file:" + temp.getName());
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    Log.i(TAG, " delete dir:" + temp.getName());
                    delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                }
            }
        }
        return true;
    }
}