package com.wise.sun.androidtools.FileUtils;
import android.util.Log;

import javax.crypto.Cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wise on 2019/8/1.
 */


class AES_Utils {
    private static final String TAG = AES_Utils.class.getSimpleName();
    private final String mDak;
    private final String mDav;
    private Cipher mEncodeCipher;
    private Cipher mDecodeCipher;

    public AES_Utils(String dak, String dav) {
        this.mDak = dak;
        this.mDav = dav;
    }

    public byte[] encrypt(byte[] array) {
        try {
            byte[] bytes = this.getEncodeCipher().doFinal(array);
            Log.d(TAG, "encrypt ok! ");
            return bytes;
        } catch (Exception var3) {
            throw new RuntimeException("encrypt error! ", var3);
        }
    }

    public byte[] decrypt(byte[] array) {
        try {
            byte[] bytes = this.getDecodeCipher().doFinal(array);
            Log.d(TAG, "decrypt ok! ");
            return bytes;
        } catch (Exception var3) {
            throw new RuntimeException("decrypt error! ", var3);
        }
    }

    private Cipher getEncodeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        if(this.mEncodeCipher == null) {
            this.mEncodeCipher = this.getCipher(1);
        }

        return this.mEncodeCipher;
    }

    private Cipher getDecodeCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        if(this.mDecodeCipher == null) {
            this.mDecodeCipher = this.getCipher(2);
        }

        return this.mDecodeCipher;
    }

    private Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return this.getCipher(mode, this.mDak, this.mDav);
    }

    private Cipher getCipher(int mode, String dak, String dav) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(dak.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(dav.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, keySpec, iv);
        return cipher;
    }
}

