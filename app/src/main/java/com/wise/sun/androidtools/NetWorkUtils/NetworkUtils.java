package com.wise.sun.androidtools.NetworkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wise.sun.androidtools.Common.Constant;

/**
 * Created by wise on 2019/5/25.
 * {@link #isNetworkConnected()}
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


}
