package com.example.cryptoloader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.loader.content.AsyncTaskLoader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    private String firstName;
    public static final String ARG_WORD = "word";
    public static final String ARG_CIPHER = "cipher";
    public static final String ARG_KEY = "Key";

    private Bundle args;


    public MyLoader(Context context, Bundle args) {
        super(context);
        if(args != null)
            this.firstName = args.getString(ARG_WORD);
        this.args = args;
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
    @Override
    public String loadInBackground() {

        byte[] cryptText = args.getByteArray(ARG_WORD);
        byte[] key = args.getByteArray(ARG_KEY);
        SecretKey OriginKey = new SecretKeySpec(key, 0, key.length, "AES");
        return decryptMsg(cryptText, OriginKey);
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secretKey) {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(cipherText));

        }catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }
}
