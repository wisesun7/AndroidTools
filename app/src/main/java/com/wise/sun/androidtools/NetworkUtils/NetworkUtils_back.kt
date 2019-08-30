package com.wise.sun.androidtools.NetworkUtils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.SystemClock
import android.util.Log

import com.wise.sun.androidtools.FileUtils.FileUtils

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by wise on 2019/5/25.
 * [是否连接网络（网络不一定通）][.isNetworkConnected]
 * [网络连接方式（有线/无限）][.connectWay]
 * [ping外网，可验证是否连通][.ping]
 * [POST上传数据][.uploadData]
 */

class NetworkUtils_back private constructor() {
    private var mContext: Context? = null


    fun isNetworkConnected(): Boolean {
            val cm = mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (null == cm) {
                Log.d(TAG, "ConnectivityManager is null!")
                return false
            } else {
                val mNetworkInfo = cm.allNetworks
                if (null != mNetworkInfo) {
                    for (network in mNetworkInfo) {
                        val networkInfo = cm.getNetworkInfo(network)
                        if (networkInfo != null) {
                            if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }

    fun connectWay(context: Context): String {
        val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (mConnectivityManager == null) {
            Log.d(TAG, "connectivityManager == null")
            return "null"
        } else {
            val networkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (networkInfo != null) {
                if (networkInfo.isConnected) {
                    return "无线"
                }
            } else {
                if (isNetworkAvalibleByApi(context)) {
                    return "有线"
                }
            }

        }
        return "null"

    }


    /**
     *
     * @param NET_TEST_PATH 下载本地路径
     * @param NET_TEST_URL 远程下载路径
     * @return
     */
    fun checkNetSpeed(NET_TEST_PATH: String, NET_TEST_URL: String): Float {
        var testSpeed = 0f
        var fileLength = 0
        var startTime: Long = 0
        var endTime: Long = 0
        var middleTime: Long = 0
        val fileName = "test.apk"
        var conn: HttpURLConnection? = null
        var `is`: InputStream? = null
        var fos: FileOutputStream? = null
        val tmpFile = File(NET_TEST_PATH)
        if (!tmpFile.exists()) {
            tmpFile.mkdir()
        }
        val file = File(NET_TEST_PATH + fileName)
        try {
            val url = URL(NET_TEST_URL)
            try {
                conn = url.openConnection() as HttpURLConnection
                fileLength = conn.contentLength
                if (fileLength <= 0) {
                    Log.d(TAG, "fileLength <= 0")
                    return 0f
                }
                startTime = SystemClock.uptimeMillis() / 1000
                //startTime = System.currentTimeMillis() / 1000;
                `is` = conn.inputStream
                fos = FileOutputStream(file)
                val buf = ByteArray(256)
                conn.connect()
                if (conn.responseCode >= 400) {
                    Log.d(TAG, "conn.getResponseCode() = " + conn.responseCode)
                    return 0f
                } else {
                    while (true) {
                        if (`is` != null) {
                            val numRead = `is`.read(buf)
                            if (numRead <= 0) {
                                break
                            } else {
                                fos.write(buf, 0, numRead)
                            }
                        } else {
                            break
                        }
                        middleTime = SystemClock.uptimeMillis() / 1000
                        if (middleTime - startTime >= 20) {
                            break
                        }
                    }
                }
                endTime = SystemClock.uptimeMillis() / 1000
                //endTime = System.currentTimeMillis() / 1000;
                Log.d(TAG, "endTime = " + endTime)
            } catch (e: IOException) {
                Log.e(TAG, "error message : " + e.message)
            } finally {
                if (conn != null) {
                    conn.disconnect()
                }
                try {
                    if (fos != null) {
                        fos.close()
                    }
                    if (`is` != null) {
                        `is`.close()
                    }
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        val readyFile = File(NET_TEST_PATH + fileName)
        val downLength = readyFile.length()
        Log.d(TAG, "downLength : " + downLength / 1024 / 1024)
        FileUtils.deleteFile(NET_TEST_PATH + fileName)
        Log.d(TAG, "fileLength" + fileLength / (1024 * 1024))
        Log.d(TAG, "spend time" + (endTime - startTime))
        if (endTime - startTime > 0) {
            testSpeed = (downLength / 1024 / (endTime - startTime)).toFloat()
        } else {
            testSpeed = 0f
        }

        return testSpeed
    }

    /**
     * @param url : 服务器地址
     * @param data : 上传的数据
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun uploadData(url: String, data: ByteArray) {
        Log.d(TAG, "upload: " + url + ", length = " + data.size)
        var connection: HttpURLConnection? = null
        var out: DataOutputStream? = null

        try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doInput = true
            connection.doOutput = true
            connection.connectTimeout = 6000
            connection.readTimeout = 6000
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.connect()
            out = DataOutputStream(connection.outputStream)
            out.write(data)
            out.flush()
            val responseCode = connection.responseCode
            Log.d(TAG, url + " RESPONSE " + responseCode)
            if (responseCode != 200) {
                throw IOException("Response code: " + responseCode)
            }
        } finally {

            if (out != null) {
                out.close()
            }
            if (connection != null) {
                connection.disconnect()
            }

        }

    }

    companion object {
        private val TAG = "NetworkUtils"
        private val mInstance = NetworkUtils_back()

        fun getInstance(context: Context): NetworkUtils_back {
            mInstance.mContext = context
            return mInstance
        }

        fun isNetworkAvalibleByApi(context: Context): Boolean {
            // 获得网络状态管理器
            val mConnectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (mConnectivityManager == null) {
                Log.d(TAG, "connectivityManager == null")
                return false
            } else {
                val net_info = mConnectivityManager.allNetworkInfo
                if (net_info != null) {
                    for (i in net_info.indices) {
                        // 判断获得的网络状态是否是处于连接状态
                        if (net_info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun ping(): Boolean {
            var result: String? = null
            try {
                // ping网址3次
                val p = Runtime.getRuntime().exec("ping -c 3 -w 100 www.baidu.com")
                // ping的状态
                val status = p.waitFor()
                if (status == 0) {
                    result = "success"
                    return true
                } else {
                    result = "failed"
                }
            } catch (e: IOException) {
                result = "IOException"
            } catch (e: InterruptedException) {
                result = "InterruptedException"
            } finally {
                Log.d(TAG, "ping result = " + result!!)
            }
            return false
        }
    }
}
