package com.appyfurious.validation

import android.content.Context
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.log.Logger
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ValidationCallback(private val product: InAppProduct,
                         private val secretKey: String,
                         private val validationListener: ValidationListener? = null,
                         private val restoreListener: RestoreListener? = null)
    : Callback<ResponseBody> {

    private var encryptor: CryptoAES128? = null

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        val body = response.body()?.string()
        if (response.isSuccessful) {
            encryptor = CryptoAES128(secretKey)
            try {
                val decryptString = encryptor?.decrypt(body)
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
                    validationFailure()
                }
            } catch (e: IOException) {
                Logger.notify("IOException validationSuccess")
                Logger.exception(e)
                validationSuccess()
            } catch (e: JSONException) {
                Logger.notify("JSONException validationSuccess")
                Logger.exception(e)
                validationSuccess()
            } catch (e: Exception) {
                Logger.notify("Exception validationSuccess")
                validationSuccess()
            }
        } else if (response.code() in 400..499) {
            Logger.notify("if (response.code() in 400..499) onValidationFailure")
            validationFailure()
        } else {
            Logger.notify("response.isSuccessful == false onValidationFailure")
            //val decryptString = encryptor?.decrypt(body)
            //Logger.notify("isNotNull: ${decryptString != null}, decryptString: $decryptString")
            //if ((response.code() in 500..599)) {
            Logger.notify("((response.code() in 500..599)) validationSuccess")
            validationSuccess()
            // } else {
            //   validationFailure()
            //}
        }
        validationListener?.onValidationHideProgress()
        Logger.notify("response body: $body")
        Logger.notify("response all: $response")
        //val headers = response.headers().toMultimap().map { "${it.key} + ${it.value}" }.joinToString(", ")
        //Logger.notify(headers)
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Logger.notify("onFailure validationSuccess")
        validationSuccess()
        validationListener?.onValidationHideProgress()
    }

    private fun validationSuccess() {
        validationListener?.onValidationSuccess()
        restoreListener?.validationRestoreSuccess()
    }

    private fun validationFailure(errorMessage: String = "Validation Error") {
        validationListener?.onValidationFailure(errorMessage)
        restoreListener?.validationRestoreFailure(errorMessage)
    }

    interface ValidationListener {
        fun validationContext(): Context?
        fun onValidationSuccess()
        fun onValidationFailure(errorMessage: String)
        fun onValidationShowProgress()
        fun onValidationHideProgress()
    }

    interface RestoreListener {
        fun validationRestoreSuccess()
        fun validationRestoreFailure(errorMessage: String)
    }
}