package com.appyfurious.validation.event

import android.content.Context
import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

/**
 * FacebookInteractor.java
 * getfitandroid
 *
 *
 * Created by o.davidovich on 24.05.2018.
 *
 *
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

object FacebookInteractor {

    const val PREMIUM_SCREEN_SHOWN = "Premium_Screen_Shown"
    const val PREMIUM_OPTION_SELECTED = "Premium_Option_Selected"

    private const val SEGMENT_ID = "Segment ID"
    private const val SCREEN_ID = "Screen ID"
    private const val SOURCE = "Source"
    private const val PRODUCT_ID = "Product ID"
    private const val PREMIUM_OPTION_PURCHASED = "Premium_Option_Purchased"

    private fun logger(context: Context?, body: (AppEventsLogger) -> Unit) {
        context?.let {
            body(AppEventsLogger.newLogger(it))
        }
    }

    fun logAddedToCartEvent(context: Context?) {
        logger(context) {
            it.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART)

        }
    }

    fun logPremiumScreenShownEvent(context: Context?, screenName: String, callScreenName: String) {
        logger(context) {
            it.logEvent(PREMIUM_SCREEN_SHOWN, Bundle().apply {
                putString(SEGMENT_ID, "default")
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
            })
        }
    }

    fun logPremiumOptionSelectedEvent(context: Context?, screenName: String, callScreenName: String, productId: String) {
        logger(context) {
            it.logEvent(PREMIUM_OPTION_SELECTED, Bundle().apply {
                putString(SEGMENT_ID, "default")
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                putString(PRODUCT_ID, productId)
            })
        }
    }

    fun logPremiumOptionPurchasedEvent(context: Context?, screenName: String, callScreenName: String, productId: String) {
        logger(context) {
            it.logEvent(PREMIUM_OPTION_SELECTED, Bundle().apply {
                putString(SEGMENT_ID, "default")
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                putString(PRODUCT_ID, productId)
            })
        }
    }
}