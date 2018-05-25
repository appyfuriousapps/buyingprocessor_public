package com.appyfurious.validation;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * CryptoAES128.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class CryptoAES128 {

    private SecretKeySpec mSecretKeySpec;

    public CryptoAES128(String secretKey) {
        try {
            mSecretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid key");
        }
    }

    public String encrypt(String requestBody) {
        byte[] encodedBytes;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, mSecretKeySpec);
            encodedBytes = c.doFinal(requestBody.getBytes());
            byte[] original = Base64.encode(encodedBytes, Base64.DEFAULT);
            return new String(original);
        } catch (Exception e) {
            return "";
        }
    }

    public String decrypt(String responseBody) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, mSecretKeySpec);
            byte[] bytes = Base64.decode(responseBody.getBytes("UTF-8"), Base64.DEFAULT);
            byte[] b = c.doFinal(bytes);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

}