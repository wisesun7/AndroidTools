package com.wise.sun.androidtools.FileUtils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wise on 2019/6/25.
 * {@link #copyFile(Uri, Uri, String)}
 * {@link #copyFiles(ArrayList, String, String)}
 * {@link #cancelTask(String, boolean)}
 * {@link #cancleAllTask(boolean)}
 * {@link #onCopyStarted(String, Uri, Uri)}
 * {@link #onCopyFinished(String, Uri, Uri)}
 * {@link #onProgressUpdate(String, Uri, Uri, int)}
 * {@link #onCopyError(String, Uri, Uri)}
 * {@link #registerListener(CopyFileTask.CopyFileStateListener)}
 * {@link #unregisterListener(CopyFileTask.CopyFileStateListener)}
 * {@link #getFileName(File)}
 */

public class CopyFileManager implements CopyFileTask.CopyFileStateListener {
    public static volatile CopyFileManager instance;
    HashMap<String, CopyFileTask> taskHashMap = new HashMap<String, CopyFileTask>();
    Context context;
    public final String defaultPath;
    HashSet<CopyFileTask.CopyFileStateListener> listeners = new HashSet<CopyFileTask.CopyFileStateListener>();
    public ExecutorService serial_exectur = Executors.newSingleThreadExecutor();

    private CopyFileManager(Context context) {
        this.context = context;
        taskHashMap.clear();
        defaultPath = context.getFilesDir().getPath();
    }

    public synchronized static CopyFileManager getInstance(Context context) {
        if (instance == null && context == null)
            throw new IllegalStateException("manager not init");
        if (instance == null) {
            instance = new CopyFileManager(context);
        }
        return instance;
    }

    public void copyFile(@NonNull Uri src, @Nullable Uri target, @Nullable String name) throws
            FileNotFoundException {
        File file = new File(src.getPath());
        String taskName = name + src.toString();
        if (name == null && name.isEmpty()) {
            taskName = src.toString();
        }
        if (file.exists()) {
            CopyFileTask fileTask = new CopyFileTask(taskName, this);
            taskHashMap.put(taskName, fileTask );
            fileTask.executeOnExecutor(serial_exectur, src, target);

        } else {
            throw new FileNotFoundException(src.toString() + "not found");
        }


    }

    public void copyFiles(@NonNull ArrayList<String> srcs, @Nullable String targetDir, @NonNull String name)
            throws
            FileNotFoundException {
        String filePath = targetDir;
        if (targetDir == null || targetDir.isEmpty()) {
            filePath = defaultPath;
        }
        File tarDir = new File(filePath);
        if (!tarDir.exists())
            tarDir.mkdirs();
        for (String path :srcs) {
            File file = new File(path);
            Uri targetUri = Uri.fromFile(new File(filePath + "/" + file.getName()));
            copyFile(Uri.fromFile(new File(path)), targetUri, name);
        }
    }

    public void cancelTask(String name, boolean interrupt) {
        Iterator iter = taskHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            CopyFileTask copyFileTask = (CopyFileTask) entry.getValue();
            if (key.contains(name)) {
                copyFileTask.cancel(interrupt);
            }
        }
    }


    public void cancleAllTask(boolean interrupt) {
        Iterator iter = taskHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            CopyFileTask copyFileTask = (CopyFileTask) entry.getValue();
//            if (key.contains(name)) {
            copyFileTask.cancel(interrupt);
//            }
        }
        taskHashMap.clear();
        serial_exectur.shutdown();
        serial_exectur = Executors.newSingleThreadExecutor();
    }


    @Override
    public void onCopyStarted(String name, Uri src, Uri target) {
        if (!taskHashMap.containsKey(name)) return;
        for (CopyFileTask.CopyFileStateListener listener :
                listeners) {
            listener.onCopyStarted(name, src, target);
        }
    }

    @Override
    public void onProgressUpdate(String name, Uri src, Uri target, int progress) {
        if (!taskHashMap.containsKey(name)) return;
        for (CopyFileTask.CopyFileStateListener listener :
                listeners) {
            listener.onProgressUpdate(name, src, target, progress);
        }
    }

    @Override
    public void onCopyFinished(String name, Uri src, Uri target) {
        if (!taskHashMap.containsKey(name)) return;
        for (CopyFileTask.CopyFileStateListener listener :
                listeners) {
            listener.onCopyFinished(name, src, target);
        }
    }

    @Override
    public void onCopyError(String name, Uri src, Uri target) {
        if (!taskHashMap.containsKey(name)) return;
        for (CopyFileTask.CopyFileStateListener listener :
                listeners) {
            listener.onCopyError(name, src, target);
        }
    }

    String getFileName(File file) {
        return file.getName();
    }

    public void registerListener(CopyFileTask.CopyFileStateListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(CopyFileTask.CopyFileStateListener listener) {
        listeners.remove(listener);
    }


}
