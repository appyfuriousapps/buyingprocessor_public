package com.appyfurious.validation

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
        val c = Cipher.getInstance("AES")
        c.init(Cipher.ENCRYPT_MODE, mSecretKeySpec)
        val encodedBytes = c.doFinal(requestBody.toByteArray())
        Logger.notify("encodedBytes $encodedBytes")
        val original = Base64.encode(encodedBytes, Base64.DEFAULT)
        Logger.notify("original $original")
        String(original)
    } catch (e: Exception) {
        ""
    }

    fun decrypt(responseBody: String?) = try {
        val c = Cipher.getInstance("AES/ECB/NoPadding")
        c.init(Cipher.DECRYPT_MODE, mSecretKeySpec)
        val bytes = Base64.decode(responseBody?.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        val b = c.doFinal(bytes)
        String(b)
    } catch (e: Exception) {
        null
    }
}