package com.wise.sun.androidtools.NetWorkUtils;

import android.os.AsyncTask;

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

    private AsyncTask mAsyncTask = new AsyncTask() {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    };


}
