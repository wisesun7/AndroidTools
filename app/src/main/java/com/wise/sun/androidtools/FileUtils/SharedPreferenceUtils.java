package com.wise.sun.androidtools.FileUtils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by wise on 2019/6/25.
 * {@link #put(String, Object)}
 * {@link #get(String, Object)}
 * {@link #clear()}
 * {@link #remove(String)}
 * {@link #contains(String)}
 * {@link #getAll()}
 */

public class SharedPreferenceUtils {

    private static final String TAG = SharedPreferenceUtils.class.getSimpleName();
    private static Context mContext;

    /**
     * sharedprefs的文件名(自定义)
     */
    public static final String FILE_NAME = "androd_tools_sharedprefs";


    private SharedPreferenceUtils(){
    }

    private static class SharedPrefsUtilHolder {
        private static final SharedPreferenceUtils INSTANCE = new SharedPreferenceUtils();
    }

    public static SharedPreferenceUtils getInstance(Context context){
        mContext = context.getApplicationContext();
        return SharedPrefsUtilHolder.INSTANCE;
    }
    /**
     * 保存数据的方法,我们需要拿到保存数据的具体类型,然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public boolean put(String key, Object object) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        return editor.commit();
    }
    /**
     * 得到保存数据的方法,我们根据默认值得到保存的数据的具体类型,然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }
    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public boolean remove(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }
    /**
     * 清除所有数据
     */
    public boolean clear() {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }
    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }
    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map getAll() {
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

}
