package com.appyfurious.analytics

import android.content.Context
import android.os.Bundle
import com.appsflyer.AppsFlyerLib
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.log.Logger
//import com.crashlytics.android.answers.Answers
//import com.crashlytics.android.answers.CustomEvent
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics

object Events {

    class Param(val key: String, val value: String)

    const val PREMIUM_SCREEN_SHOWN = "Premium_Screen_Shown"
    const val PREMIUM_OPTION_SELECTED = "Premium_Option_Selected"
    const val PREMIUM_OPTION_PURCHASED = "Premium_Option_Purchased"

    const val SEGMENT_ID = "Segment_ID"
    const val SCREEN_ID = "Screen_ID"
    const val SOURCE = "Source"
    const val PRODUCT_ID = "Product_ID"

    const val DEFAULT_VALUE = "default"

    const val AF_PURCHASE = "af_purchase"
    const val AF_PRICE = "af_price"

    private fun facebook(context: Context?, body: (AppEventsLogger) -> Unit) {
        context?.let {
            if (Analytics.isInitFacebook)
                body(AppEventsLogger.newLogger(it))
        }
    }

    private fun firebase(context: Context?, body: (FirebaseAnalytics) -> Unit) {
        context?.let {
            body(FirebaseAnalytics.getInstance(it))
        }
    }

    fun logPurchaseEvents(context: Context?, product: InAppProduct) {
        facebook(context) {
            val bundle = Bundle().apply {
                putString(AppEventsConstants.EVENT_PARAM_CONTENT, product.title)
                putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, product.productId)
                putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, product.type)
                putString(AppEventsConstants.EVENT_PARAM_CURRENCY, product.priceCurrencyCode)
            }
            it.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, product.getPriceParse(), bundle)
            Logger.notify("""
                |ADDED_TO_CART eventName: ${AppEventsConstants.EVENT_NAME_ADDED_TO_CART}
                |${AppEventsConstants.EVENT_PARAM_CONTENT}, ${product.title}
                |${AppEventsConstants.EVENT_PARAM_CONTENT_ID}, ${product.productId}
                |${AppEventsConstants.EVENT_PARAM_CONTENT_TYPE}, ${product.type}
                |${AppEventsConstants.EVENT_PARAM_CURRENCY}, ${product.priceCurrencyCode}
                |price: ${product.getPriceParse()}, original: ${product.price}
            """.trimMargin())
            Logger.notify("logPurchaseEvents ADDED_TO_CART")
        }

        if (Analytics.isInitAppsflyer) {
            val map = HashMap<String, Any>()
            map[AF_PRICE] = product.getPriceParse()
            AppsFlyerLib.getInstance().trackEvent(context, AF_PURCHASE, map)
            Logger.notify("logPurchaseEvents AF_PURCHASE $AF_PURCHASE price: ${map[AF_PRICE]}")
        }

        Logger.notify("logPurchaseEvents price: ${product.getPriceParse()}")
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
        val bundle = Bundle().apply {
            putString(SEGMENT_ID, segment)
            putString(SCREEN_ID, screenName)
            putString(SOURCE, callScreenName)
            if (productId != null) {
                putString(PRODUCT_ID, productId)
            }
        }
        facebook(context) {
            it.logEvent(eventName, bundle)
        }
        firebase(context) {
            it.logEvent(eventName, bundle)
        }
//        if (Analytics.isInitFabric) {
//            val event = CustomEvent(eventName)
//                    .putCustomAttribute(SEGMENT_ID, segment)
//                    .putCustomAttribute(SCREEN_ID, screenName)
//                    .putCustomAttribute(SOURCE, callScreenName)
//            if (productId != null) {
//                event.putCustomAttribute(PRODUCT_ID, productId)
//            }
//            Answers.getInstance().logCustom(event)
//        }
    }

    fun logEventUniversal(context: Context?, eventName: String, vararg params: Param) {
        facebook(context) { l ->
            l.logEvent(eventName, Bundle().apply {
                params.map { putString(it.key, it.value) }
            })
        }
        firebase(context) { l ->
            l.logEvent(eventName, Bundle().apply {
                params.map { putString(it.key, it.value) }
            })
        }
//        if (Analytics.isInitFabric) {
//            val event = CustomEvent(eventName)
//            params.map { event.putCustomAttribute(it.key, it.value) }
//            Answers.getInstance().logCustom(event)
//        }
    }
}