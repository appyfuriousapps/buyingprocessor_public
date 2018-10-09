package com.appyfurious.log

import android.util.Log

object Logger {
    const val NOTIFY = "notify"
    const val EXCEPTION = "exception"
    const val TAG = "validation"
    const val TAG_AD = "AFAdManager"
    const val TAG_DB = "AFRealm"
    const val TAG_LC = "AFLifecycle"
    const val TAG_SH = "AFPreferences"
    const val TAG_DM = "AFDataManager"
    const val TAG_PC = "AFProducts"
    const val TAG_AS = "AFAppSee"
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

    fun logDbChange(message: String) {
        if (isDebug) {
            Log.d(TAG_DB, message)
        }
    }

    fun logMoveToForeground(message: String) {
        if (isDebug) {
            Log.d(TAG_LC, message)
        }
    }

    fun logMoveToBackground(message: String) {
        if (isDebug) {
            Log.d(TAG_LC, message)
        }
    }

    fun logSharedPreferences(message: String) {
        if (isDebug) {
            Log.d(TAG_SH, message)
        }
    }

    fun logDataManager(message: String) {
        if (isDebug) {
            Log.d(TAG_DM, message)
        }
    }

    fun logRatingIdConfigChanged(message: String) {
        if (isDebug) {
            Log.d(TAG_PC, message)
        }
    }

    fun logAppSee(message: String) {
        if (isDebug) {
            Log.d(TAG_AS, message)
        }
    }
}