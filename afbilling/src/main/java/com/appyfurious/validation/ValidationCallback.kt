package com.appyfurious.validation

import com.appyfurious.log.Logger
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidationCallback(private val secretKey: String,
                         private val validationListener: ValidationListener? = null,
                         private val restoreListener: RestoreListener? = null)
    : Callback<ResponseBody> {

    private var encryptor: CryptoAES128? = null

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        val body = response.body()?.string()
        when {
            response.code() in 500..599 -> {
                Logger.notify("response.code() in 500..599 validationSuccess")
                validationSuccess()
            }
            else -> {
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
                } catch (e: Exception) {
                    Logger.exception("Exception onValidationFailure")
                    Logger.exception(e)
                    validationFailure("Exception onValidationFailure")
                }
            }
        }
        validationListener?.onValidationHideProgress()
        Logger.notify("response body: $body")
        Logger.notify("response all: $response")
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Logger.notify("onFailure validationFailure")
        validationFailure("onFailure validationFailure")
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