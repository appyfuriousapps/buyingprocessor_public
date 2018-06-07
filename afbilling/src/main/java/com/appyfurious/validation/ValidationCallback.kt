package com.appyfurious.validation

import android.content.Context

import com.appyfurious.log.Logger
import com.appyfurious.validation.event.FacebookInteractor
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ValidationCallback.java
 * getfitandroid
 *
 *
 * Created by o.davidovich on 25.05.2018.
 *
 *
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

class ValidationCallback(private val mSecretKey: String,
                         private val mValidationListener: ValidationListener) : Callback<ResponseBody> {

    private var mEncryptor: CryptoAES128? = null

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        Logger.notify("onResponse ${response.body()?.string()}")
        Logger.notify("code ${response.code()}, message ${response.message()}")
        if (response.isSuccessful) {
            Logger.notify("response.isSuccessful")
            mEncryptor = CryptoAES128(mSecretKey)
            try {
                val decryptString = mEncryptor?.decrypt(response.body()?.string())
                Logger.notify("isNotNull: ${decryptString != null}, decryptString: $decryptString")
                val jsonResponse = JSONObject(decryptString)
                Logger.notify(jsonResponse.toString())
                val jsonData = jsonResponse.getJSONObject("data")
                Logger.notify(jsonData.toString())
                val isValid = jsonData.getBoolean("isValid")
                Logger.notify(isValid.toString())
                if (isValid) {
                    Logger.notify("isValid validationSuccess")
                    validationSuccess()
                } else {
                    Logger.notify("isValid == false onValidationFailure")
                    mValidationListener.onValidationFailure("Validation Error")
                }
            } catch (e: IOException) {
                Logger.notify("IOException validationSuccess")
                Logger.exception(e)
                validationSuccess()
            } catch (e: JSONException) {
                Logger.notify("JSONException validationSuccess")
                Logger.exception(e)
                validationSuccess()
            }
        } else {
            Logger.notify("response.isSuccessful == false onValidationFailure")
            mValidationListener.onValidationFailure("Validation Error")
        }
        mValidationListener.onValidationHideProgress()
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Logger.notify("onFailure validationSuccess")
        validationSuccess()
        mValidationListener.onValidationHideProgress()
    }

    private fun validationSuccess() {
        mValidationListener.onValidationSuccess()
        FacebookInteractor.logAddedToCartEvent(mValidationListener.validationContext())
    }

    interface ValidationListener {
        fun validationContext(): Context?
        fun onValidationSuccess()
        fun onValidationFailure(errorMessage: String)
        fun onValidationShowProgress()
        fun onValidationHideProgress()
    }

}