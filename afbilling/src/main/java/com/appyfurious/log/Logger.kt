package com.appyfurious.log

import android.util.Log

object Logger {
    const val NOTIFY = "notify"
    const val EXCEPTION = "exception"
    const val TAG = "validation"
    const val TAG_AD = "AdManager"
    private var isDebug = false
    private var isPrintExceptionObject = false

    fun init(isDebug: Boolean, isPrintExceptionObject: Boolean) {
        this.isDebug = isDebug
        this.isPrintExceptionObject = isPrintExceptionObject
    }

    fun notify(message: String) {
        if (isDebug) {
            Log.d(TAG, "$NOTIFY: $message")
        }
    }

    fun exception(message: String) {
        if (isDebug) {
            Log.e(TAG, "$EXCEPTION: $message")
        }
    }

    fun exception(ex: Exception) {
        if (isDebug && isPrintExceptionObject) {
            Log.e(TAG, ex.message, ex)
        }
    }

    fun print(message: String) {
        if (isDebug) {
            Log.d(TAG, message)
        }
    }

    fun print(key: String, message: String) {
        if (isDebug) {
            Log.d(TAG, "$key: $message")
        }
    }

    fun logAd(message: String) {
        if (isDebug) {
            Log.d(TAG_AD, message)
        }
    }
}