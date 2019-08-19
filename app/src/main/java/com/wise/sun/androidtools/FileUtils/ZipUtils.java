package com.wise.sun.androidtools.FileUtils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by wise on 2019/6/19.
 * {@link #zipFile(File, ZipOutputStream, String)}
 * {@link #zipFiles(Collection, File)}
 * {@link #zipFiles(Collection, File, String)}
 */

public class ZipUtils {
    private static final String TAG = ZipUtils.class.getSimpleName();
    private static final int BUFF_SIZE = 1024 * 1024;  //1M

    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile) throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.close();
    }


    /**
     * 批量压缩文件（夹）
     *
     * @param resFileList 要压缩的文件（夹）列表
     * @param zipFile 生成的压缩文件
     * @param comment 压缩文件的注释
     * @throws IOException 当压缩过程出错时抛出
     */
    public static void zipFiles(Collection<File> resFileList, File zipFile, String comment) throws IOException {
        ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipout, "");
        }
        zipout.setComment(comment);
        zipout.close();
    }


    /**
     * 压缩文件
     *
     * @param resFile 需要压缩的文件（夹）
     * @param zipout 压缩的目的文件
     * @param rootpath 压缩的文件路径
     * @throws FileNotFoundException 找不到文件时抛出
     * @throws IOException 当压缩过程出错时抛出
     */
    private static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException, IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator)+ resFile.getName();
        rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    zipFile(file, zipout, rootpath);
                }
            }
        } else {
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),BUFF_SIZE);
            zipout.putNextEntry(new ZipEntry(rootpath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipout.write(buffer, 0, realLength);
            }
            in.close();
            zipout.flush();
            zipout.closeEntry();
        }
    }

    /**
     * 压缩字节数组
     * @param array 字节数组
     * @return
     */
    public byte[] compress(byte[] array) {
        new ByteArrayOutputStream();
        GZIPOutputStream zip = null;

        ByteArrayOutputStream baos;
        try {
            baos = new ByteArrayOutputStream();
            zip = new GZIPOutputStream(baos);
            zip.write(array);
            zip.flush();
        } catch (IOException var8) {
            throw new RuntimeException("compress error! ", var8);
        } finally {
            close(new Closeable[]{zip});
        }

        byte[] bytes = baos.toByteArray();
        Log.d(TAG, "compress ok, " + array.length + " to " + bytes.length);
        return bytes;
    }

    /**
     * 解压字节数组
     * @param array 字节数组
     * @return
     */
    public byte[] uncompress(byte[] array) {
        ByteArrayInputStream bais = null;
        GZIPInputStream zip = null;
        ByteArrayOutputStream baos = null;

        try {
            bais = new ByteArrayInputStream(array);
            zip = new GZIPInputStream(bais);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int offset;
            while((offset = zip.read(buffer)) != -1) {
                baos.write(buffer, 0, offset);
            }

            byte[] bytes = baos.toByteArray();
            Log.d(TAG, "uncompress ok, " + array.length + " to " + bytes.length);
            byte[] var8 = bytes;
            return var8;
        } catch (IOException var12) {
            throw new RuntimeException("uncompress error! ", var12);
        } finally {
            close(new Closeable[]{bais, zip, baos});
        }
    }

    public static final void close(Closeable... closeables) {
        if(closeables != null) {
            Closeable[] var1 = closeables;
            int var2 = closeables.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Closeable closeable = var1[var3];
                if(closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException var6) {
                        ;
                    }
                }
            }

        }
    }
}
