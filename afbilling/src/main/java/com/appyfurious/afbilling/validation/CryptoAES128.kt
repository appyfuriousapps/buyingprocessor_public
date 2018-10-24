package com.appyfurious.afbilling.validation

import android.annotation.SuppressLint
import android.util.Base64
import com.appyfurious.log.Logger

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * CryptoAES128.java
 * getfitandroid
 *
 *
 * Created by o.davidovich on 25.05.2018.
 *
 *
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

class CryptoAES128(secretKey: String) {

    private var mSecretKeySpec: SecretKeySpec? = null

    init {
        try {
            Logger.notify("init start")
            Logger.notify("secret key: $secretKey")
            mSecretKeySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
            Logger.notify("init finish")
        } catch (e: Exception) {
            Logger.exception("init")
            Logger.exception(e)
            throw IllegalArgumentException("Invalid key")
        }
    }

    fun encrypt(requestBody: String) = try {
        Logger.notify("encrypt")
        Logger.notify("body request: $requestBody")
        val c = Cipher.getInstance("AES")
        c.init(Cipher.ENCRYPT_MODE, mSecretKeySpec)
        val encodedBytes = c.doFinal(requestBody.toByteArray())
        Logger.notify("encodedBytes ${String(encodedBytes)}")
        val original = Base64.encode(encodedBytes, Base64.DEFAULT)
        Logger.notify("original  ${String(original)}")
        String(original)
    } catch (e: Exception) {
        ""
    }

    @SuppressLint("GetInstance")
    fun decrypt(responseBody: String?) = try {
        Logger.notify("responseBody $responseBody")
        val c = Cipher.getInstance("AES/ECB/NoPadding")
        c.init(Cipher.DECRYPT_MODE, mSecretKeySpec)
        val bytes = Base64.decode(responseBody?.toByteArray(), Base64.DEFAULT)
        Logger.notify("decrypt bytes: ${String(bytes)}")
        val b = c.doFinal(bytes)
        Logger.notify("decrypt value: ${String(b)}")
        String(b)
    } catch (e: Exception) {
        null
    }
}