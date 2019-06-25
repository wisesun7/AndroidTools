package com.wise.sun.androidtools.FileUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wise on 2019/6/25.
 * example : {@link CopyFileManager}
 */

public class CopyFileTask extends AsyncTask<Uri, Integer, String> {
    private final CopyFileStateListener listener;
    private String requestString;
    private Uri src, target;
    private static final String TMP_END_STR = "_tmp";

    public CopyFileTask(String name, @NonNull CopyFileStateListener listener) {
        this.name = name;
        this.listener = listener;
    }

    public interface CopyFileStateListener {
        void onCopyStarted(String name, Uri src, Uri target);

        void onProgressUpdate(String name, Uri src, Uri target, int progress);

        void onCopyFinished(String name, Uri src, Uri target);

        void onCopyError(String name, Uri src, Uri target);
    }


    private final String name;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onCopyStarted(name, src, target);
    }

    @Override
    protected String doInBackground(Uri... params) {
        if (isCancelled()) return null;
        requestString = params[0].getPath();
        src = params[0];
        target = params[1];
        File oldFile = new File(params[0].getPath());
        File targetFile = new File(params[1].getPath() + TMP_END_STR);

        try {
            if (!targetFile.getParentFile().exists()) oldFile.mkdirs();
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            } else {
                targetFile.delete();
            }
            return copyFile(oldFile.getAbsolutePath(), targetFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null) {
            if (isCancelled()) return;
            listener.onCopyError(name, src, target);
        } else {
            listener.onCopyFinished(name, src, target);
        }
    }

    public String copyFile(String src, String out) {
        {
            long time = 0;
            try {
                RandomAccessFile srcFile = new RandomAccessFile(src, "r");
                File outF = new File(out);
                if (outF.exists()) {
                    outF.delete();
                }
                outF.createNewFile();
                RandomAccessFile outFile = new RandomAccessFile(out, "rw");
                try {
                    FileChannel srcChannel = srcFile.getChannel();
                    FileChannel outChannel = outFile.getChannel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    long size = srcChannel.size();
                    long times = 0;
                    while (-1 != srcChannel.read(buffer) && !isCancelled()) {
                        buffer.flip();
                        outChannel.write(buffer);
                        times++;
                        buffer.compact();
                        //TODO change the name
                        if (SystemClock.elapsedRealtime() - time > 10) {
                            time = SystemClock.elapsedRealtime();
                            publishProgress((int) Math.rint(times * 1024.0f / size * 100));
                        }
                    }
                    publishProgress((int) Math.rint(times * 1024.0f / size * 100));
                    buffer.flip();
                    outChannel.close();
                    srcChannel.close();
                    if (isCancelled() && outF.exists()) {
                        if (outF.exists()) {
                            outF.delete();
                        }
                        outF.deleteOnExit();
                        return null;
                    }
                    String target = out.substring(0, out.length() - TMP_END_STR.length());
                    File targetF = new File(target);
                    if (targetF.exists()) {
                        targetF.delete();
                    }
                    outF.renameTo(targetF);
                    return Uri.fromFile(targetF).toString();

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onProgressUpdate(name, src, target, values[0]);
    }
}

