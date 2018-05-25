package com.appyfurious.afbilling

import android.util.Log

open class LogDebug {
    fun d(message: String) {
        Log.d("TAG", "message: $message")
    }
}