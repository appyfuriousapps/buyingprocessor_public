package com.appyfurious.log

import android.util.Log

class Logger {
    companion object {
        const val NOTIFY = "notify"
        const val EXCEPTION = "exception"
        const val TAG = "validation"
        var isDebug = false

        fun notify(message: String) {
            if (isDebug) {
                Log.d(TAG, "$NOTIFY: $message")
            }
        }

        fun exception(message: String) {
            if (isDebug) {
                Log.d(TAG, "$EXCEPTION: $message")
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
    }
}