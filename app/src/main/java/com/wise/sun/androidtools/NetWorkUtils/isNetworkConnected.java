package com.wise.sun.androidtools.NetWorkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by wise on 2019/5/25.
 */

public class isNetworkConnected implements Runnable {
    private static final String TAG = "isNetworkConnected";
    private Context mContext;
    private Handler mHandler;

    public isNetworkConnected(Context context, Handler handler){
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        boolean isNetworkConnected = isNetworkConnected();
        Message msg = new Message();
        msg.obj = isNetworkConnected;
        mHandler.sendMessage(msg);
    }

    private boolean isNetworkConnected(){
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
}
