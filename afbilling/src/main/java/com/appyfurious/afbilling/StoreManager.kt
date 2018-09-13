package com.appyfurious.afbilling

import android.content.Context
import com.appyfurious.validation.ValidKeys

object StoreManager {

    fun init(context: Context, baseUrl: String, apiKey: String, secretKey: String) {
        ValidKeys.init(baseUrl, apiKey, secretKey)
    }

    private fun body(success: () -> Unit) {
        if (!ValidKeys.isNotNull())
            throw IllegalStateException("ValidKeys is not initialized (baseUrl, apiKey, secretKey)")
        success()
    }
}