package com.appyfurious.analytics

import android.content.Context
import android.os.Bundle
import com.appsflyer.AppsFlyerLib
import com.appyfurious.afbilling.InAppProduct
import com.appyfurious.log.Logger
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

object Events {

    class Param(val key: String, val value: String)

    const val PREMIUM_SCREEN_SHOWN = "Premium_Screen_Shown"
    const val PREMIUM_OPTION_SELECTED = "Premium_Option_Selected"
    const val PREMIUM_OPTION_PURCHASED = "Premium_Option_Purchased"

    const val SEGMENT_ID = "Segment ID"
    const val SCREEN_ID = "Screen ID"
    const val SOURCE = "Source"
    const val PRODUCT_ID = "Product ID"

    const val DEFAULT_VALUE = "default"

    const val AF_PURCHASE = "af_purchase"
    const val AF_PRICE = "af_price"

    private fun logger(context: Context?, body: (AppEventsLogger) -> Unit) {
        context?.let {
            body(AppEventsLogger.newLogger(it))
        }
    }

    fun logPurchaseEvents(context: Context?, product: InAppProduct) {
        val bundle = Bundle().apply {
            putString(AppEventsConstants.EVENT_PARAM_CONTENT, product.title)
            putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, product.productId)
            putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, product.getType())
            putString(AppEventsConstants.EVENT_PARAM_CURRENCY, product.priceCurrencyCode)
        }
        logger(context) {
            it.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, product.gerPriceParse(), bundle)
            Logger.notify("logPurchaseEvents ADDED_TO_CART")
        }

        val map = HashMap<String, Any>()
        map[AF_PRICE] = product.gerPriceParse()
        AppsFlyerLib.getInstance().trackEvent(context, AF_PURCHASE, map)
        Logger.notify("logPurchaseEvents AF_PURCHASE")
    }

    fun logPremiumScreenShownEvent(context: Context?, segment: String, screenName: String, callScreenName: String) {
        Logger.notify("logPremiumScreenShownEvent")
        logEvent(context, PREMIUM_SCREEN_SHOWN, segment, screenName, callScreenName, null)
    }

    fun logPremiumOptionSelectedEvent(context: Context?, segment: String, screenName: String,
                                      callScreenName: String, productId: String?) {
        Logger.notify("logPremiumOptionSelectedEvent")
        logEvent(context, PREMIUM_OPTION_SELECTED, segment, screenName, callScreenName, productId)
    }

    fun logPremiumOptionPurchasedEvent(context: Context?, segment: String, screenName: String,
                                       callScreenName: String, productId: String?) {
        Logger.notify("logPremiumOptionPurchasedEvent")
        logEvent(context, PREMIUM_OPTION_PURCHASED, segment, screenName, callScreenName, productId)
    }

    fun logEvent(context: Context?, eventName: String, segment: String, screenName: String,
                 callScreenName: String, productId: String? = null) {
        logger(context) {
            it.logEvent(eventName, Bundle().apply {
                putString(SEGMENT_ID, segment)
                putString(SCREEN_ID, screenName)
                putString(SOURCE, callScreenName)
                if (productId != null) {
                    putString(PRODUCT_ID, productId)
                }
            })
        }

//        val event = CustomEvent(eventName)
//                .putCustomAttribute(SEGMENT_ID, segment)
//                .putCustomAttribute(SCREEN_ID, screenName)
//                .putCustomAttribute(SOURCE, callScreenName)
//        if (productId != null) {
//            event.putCustomAttribute(PRODUCT_ID, productId)
//        }
//        Answers.getInstance().logCustom(event)
    }

    fun logEventUniversal(context: Context?, eventName: String, vararg params: Param) {
        logger(context) { l ->
            l.logEvent(eventName, Bundle().apply {
                params.map { putString(it.key, it.value) }
            })
        }

        val map = HashMap<String, Any>()
        params.map { map[it.key] = it.value }
        AppsFlyerLib.getInstance().trackEvent(context, eventName, map)
//
//        val event = CustomEvent(eventName)
//        params.map { event.putCustomAttribute(it.key, it.value) }
//        Answers.getInstance().logCustom(event)
    }
}