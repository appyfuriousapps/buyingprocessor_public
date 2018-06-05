package com.appyfurious.log

import android.util.Log

class Logger {
    companion object {
        const val NOTIFY = "notify"
        const val EXCEPTION = "exception"
        const val TAG = "validation"
        private var isDebug = false

        fun init(isDebug: Boolean) {
            this.isDebug = isDebug
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