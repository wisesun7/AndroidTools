package com.wise.sun.androidtools.NetworkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import com.wise.sun.androidtools.FileUtils.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wise on 2019/5/25.
 * {@link #isNetworkConnected() 是否连接网络（网络不一定通）}
 * {@link #connectWay(Context)  网络连接方式（有线/无限）}
 * {@link #ping() ping外网，可验证是否连通}
 * {@link #uploadData(String, byte[]) POST上传数据}
 */

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final NetworkUtils mInstance = new NetworkUtils();
    private Context mContext;
    private NetworkUtils(){

    }

    public static NetworkUtils getInstance(Context context){
        mInstance.mContext = context;
        return mInstance;
    }


    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);

        if (null == cm){
            Log.d(TAG,"ConnectivityManager is null!");
            return false;
        } else {
            Network[] mNetworkInfo = cm.getAllNetworks();
            if (null != mNetworkInfo) {
                for (Network network : mNetworkInfo) {
                    NetworkInfo networkInfo = cm.getNetworkInfo(network);
                    if (networkInfo != null){
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String connectWay(Context context){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager == null) {
            Log.d(TAG, "connectivityManager == null");
            return "null";
        } else {
            NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(networkInfo!=null){
                if(networkInfo.isConnected()) {
                    return "无线";
                }
            }else {
                if(isNetworkAvalibleByApi(context)){
                    return "有线";
                }
            }

        }
        return "null";

    }

    public static boolean isNetworkAvalibleByApi(Context context) {
        // 获得网络状态管理器
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager == null) {
            Log.d(TAG, "connectivityManager == null");
            return false;
        } else {
            NetworkInfo[] net_info = mConnectivityManager.getAllNetworkInfo();
            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean ping() {
        String result = null;
        try {
            // ping网址3次
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 www.baidu.com" );
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d(TAG, "ping result = " + result);
        }
        return false;
    }


    /**
     *
     * @param NET_TEST_PATH 下载本地路径
     * @param NET_TEST_URL 远程下载路径
     * @return
     */
    public float checkNetSpeed(String NET_TEST_PATH , String NET_TEST_URL) {
        float testSpeed = 0;
        int fileLength = 0;
        long startTime = 0;
        long endTime = 0;
        long middleTime = 0;
        final String fileName = "test.apk";
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        File tmpFile = new File(NET_TEST_PATH);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(NET_TEST_PATH + fileName);
        try {
            URL url = new URL(NET_TEST_URL);
            try {
                conn = (HttpURLConnection) url.openConnection();
                fileLength = conn.getContentLength();
                if (fileLength <= 0) {
                    Log.d(TAG, "fileLength <= 0");
                    return 0;
                }
                startTime = SystemClock.uptimeMillis() / 1000;
                //startTime = System.currentTimeMillis() / 1000;
                is = conn.getInputStream();
                fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                if (conn.getResponseCode() >= 400) {
                    Log.d(TAG, "conn.getResponseCode() = " + conn.getResponseCode());
                    return 0;
                } else {
                    while (true) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }
                        } else {
                            break;
                        }
                        middleTime = SystemClock.uptimeMillis()/1000;
                        if (middleTime - startTime >= 20){
                            break;
                        }
                    }
                }
                endTime = SystemClock.uptimeMillis()/1000;
                //endTime = System.currentTimeMillis() / 1000;
                Log.d(TAG, "endTime = " + endTime);
            } catch (IOException e) {
                Log.e(TAG,"error message : " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        File readyFile = new File(NET_TEST_PATH + fileName);
        long downLength =  readyFile.length();
        Log.d(TAG,"downLength : " + downLength / 1024 / 1024);
        FileUtils.deleteFile(NET_TEST_PATH + fileName);
        Log.d(TAG, "fileLength" + (fileLength / (1024 * 1024)));
        Log.d(TAG, "spend time" + (endTime - startTime));
        if ( (endTime - startTime) > 0){
            testSpeed = (downLength / (1024)) / (endTime - startTime);
        }else {
            testSpeed = 0;
        }

        return testSpeed;
    }

    /**
     * @param url : 服务器地址
     * @param data : 上传的数据
     * @throws IOException
     */
    private void uploadData(String url, byte[] data) throws IOException {
        Log.d(TAG,"upload: " + url + ", length = " + data.length);
        HttpURLConnection connection = null;
        DataOutputStream out = null;

        try {
            connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(6000);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.connect();
            out = new DataOutputStream(connection.getOutputStream());
            out.write(data);
            out.flush();
            int responseCode = connection.getResponseCode();
            Log.d(TAG,url + " RESPONSE " + responseCode);
            if(responseCode != 200) {
                throw new IOException("Response code: " + responseCode);
            }
        } finally {

            if (out != null){
                out.close();
            }
            if(connection != null) {
                connection.disconnect();
            }

        }

    }
}
