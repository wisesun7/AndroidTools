package com.wise.sun.androidtools.FileUtils;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.EventLogTags;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wise on 2019/6/19.
 * {@link #checkDirExsit(String)}
 * {@link #delAllFile(String)}
 * {@link #createFile(String, String)}
 * {@link #read(File)}
 * {@link #write(File, String)}
 * {@link #getAuthority(String)}
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 创建新文件
     * @param dir
     * @param name
     * @return
     */
    private File createFile (String dir, String name){
        String filePath = dir + "/" + name;
        Log.d(TAG,"dir is :" + filePath);
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (!parent.exists()){
            parent.mkdir();
        }
        if (!file.exists()){
            try{
                file.createNewFile();
                getAuthority(file.getPath());
                Log.d(TAG,"create file success!");
            }catch (IOException e){
                Log.d(TAG,"create file exception , message is ;" + e.getMessage());
                e.printStackTrace();
            }
        } else {

        }
        if (file.exists()){
            return file;
        } else {
            return null;
        }
    }

    /**
     * 修改文件权限
     * @param path
     */
    private void getAuthority(String path){
        try{
            Runtime.getRuntime().exec("chmod 777 " + path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     * @param file
     * @param content
     */
    private void write(@NonNull File file, String content){
        if (file == null){
            Log.e(TAG,"ERROR: file is null!");
            return;
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try{
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(content + "\\n");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (fw != null) {
                    fw.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件
     * @param file
     * @return
     */
    private ArrayList<String> read(@NonNull File file){
        if (file == null){
            Log.e(TAG,"ERROR: file is null!");
            return null;
        }
        ArrayList<String> contents = new ArrayList<String>();
        String content = "";
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((content = br.readLine()) != null){
                contents.add(content);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if (fr != null){
                    fr.close();
                }
                if (br != null){
                    br.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Log.d(TAG,"contents is :" + contents.toString());
        return contents;
    }

    /**
     * 判断目录是否存在
     * @param dir
     */
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

    /**
     * 删除文件/目录
     * @param path
     * @return
     * @throws Exception
     */
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

    /**
     * 删除单个文件
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.d(TAG,"delete file: " + fileName + " sucess!");
                return true;
            } else {
                Log.d(TAG,"delete file: " + fileName + " failed!");
                return false;
            }
        } else {
            Log.d(TAG,"delete file: " + fileName + " is not exist!");
            return false;
        }
    }
}
