package com.wise.sun.androidtools.NetworkUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wise.sun.androidtools.Common.Constant;

/**
 * Created by wise on 2019/5/25.
 */

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final NetworkUtils mInstance = new NetworkUtils();
    private NetworkUtils(){

    }

    public static NetworkUtils getInstance(){
        return mInstance;
    }

    public void execute(Runnable runnable){
        new Thread(runnable).start();
    }

    private Handler mUtilsHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.MSG_NETWORK_ISCONNECTED:
                    Log.d(TAG,"isConnected");
                    break;
            }

        }
    };


}
