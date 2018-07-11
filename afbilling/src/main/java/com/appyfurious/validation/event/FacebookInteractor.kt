package com.appyfurious.validation.event

import android.content.Context
import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

object FacebookInteractor {

    const val PREMIUM_SCREEN_SHOWN = "Premium_Screen_Shown"
    const val PREMIUM_OPTION_SELECTED = "Premium_Option_Selected"

    const val SEGMENT_ID = "Segment ID"
    const val SCREEN_ID = "Screen ID"
    const val SOURCE = "Source"
    const val PRODUCT_ID = "Product ID"
    const val PREMIUM_OPTION_PURCHASED = "Premium_Option_Purchased"

    const val DEFAULT_VALUE = "default"

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

    fun logPremiumScreenShownEvent(context: Context?, segment: String, screenName: String, callScreenName: String) {
        logger(context) {
            it.logEvent(PREMIUM_SCREEN_SHOWN, Bundle().apply {
                putString(SEGMENT_ID, segment)
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
            })
        }
    }

    fun logPremiumOptionSelectedEvent(context: Context?, segment: String, screenName: String, callScreenName: String, productId: String) {
        logger(context) {
            it.logEvent(PREMIUM_OPTION_SELECTED, Bundle().apply {
                putString(SEGMENT_ID, segment)
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                putString(PRODUCT_ID, productId)
            })
        }
    }

    fun logPremiumOptionPurchasedEvent(context: Context?, segment: String, screenName: String, callScreenName: String, productId: String) {
        logger(context) {
            it.logEvent(PREMIUM_OPTION_SELECTED, Bundle().apply {
                putString(SEGMENT_ID, segment)
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                putString(PRODUCT_ID, productId)
            })
        }
    }

    fun logEvent(context: Context?, eventName: String, segment: String, screenName: String, callScreenName: String, productId: String) {
        logger(context) {
            it.logEvent(eventName, Bundle().apply {
                putString(SEGMENT_ID, segment)
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                putString(PRODUCT_ID, productId)
            })
        }
    }
}