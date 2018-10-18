package com.appyfurious.afbilling.product

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.android.vending.billing.IInAppBillingService
import com.appsflyer.AppsFlyerLib
import com.appyfurious.afbilling.StoreManager
import com.appyfurious.afbilling.service.BillingService
import com.appyfurious.afbilling.utils.Adverting
import com.appyfurious.log.Logger
import com.appyfurious.network.manager.AFNetworkManager
import com.appyfurious.validation.body.DeviceData
import com.google.gson.GsonBuilder
import java.util.*

class ProductsManager(context: Context) {

    private val packageName = context.packageName

    private val deviceData = DeviceData(AppsFlyerLib.getInstance().getAppsFlyerUID(context)!!, "")

    fun getDeviceData(context: Context, body: (DeviceData) -> Unit) {
        Adverting(context) { idfa, isLimitAdTrackingEnabled ->
            if (isLimitAdTrackingEnabled == false) {
                deviceData.idfa = idfa
            } else {
                deviceData.idfa = null
            }
            body(deviceData)
            Logger.notify("ProductManager getDeviceData new $deviceData")
        }
    }

    fun readMyPurchases(service: IInAppBillingService?, type: String,
                        body: (products: List<MyProduct>) -> Unit) {
        Logger.notify("start readMyPurchases")
        var continuationToken: String? = null
        val gson = GsonBuilder().create()
        val myProducts = ArrayList<MyProduct>()
        try {
            do {
                val result = service?.getPurchases(3, packageName, type, continuationToken)
                if (result?.getInt("RESPONSE_CODE", -1) != 0) {
                    throw Exception("Invalid response code")
                }
                val responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                val serverProducts = responseList.map {
                    Logger.notify("readMyPurchases $it")
                    gson.fromJson(it, MyProduct::class.java)
                }
                myProducts.addAll(serverProducts)
                continuationToken = result.getString("INAPP_CONTINUATION_TOKEN")
            } while (continuationToken != null)
        } catch (ex: Exception) {
            Logger.exception("readMyPurchases Invalid response code")
            Logger.exception(ex)
        } finally {
            body(myProducts)
            Logger.notify("finish readMyPurchases")
        }
    }

    fun getInAppPurchases(service: IInAppBillingService?, productIds: List<String>, type: String = InAppProduct.SUBS): List<InAppProduct> {
        if (productIds.isEmpty()) {
            Logger.notify("productIds.isEmpty()")
            return listOf()
        }
        val skuList = ArrayList(productIds)
        val query = Bundle()
        query.putStringArrayList("ITEM_ID_LIST", skuList)
        return try {
            val skuDetails = service?.getSkuDetails(3, packageName, type, query)
            val responseList = skuDetails?.getStringArrayList("DETAILS_LIST") ?: listOf<String>()
            val gson = GsonBuilder().create()
            responseList.map {
                Logger.notify("getInAppPurchases $it")
                gson.fromJson(it, InAppProduct::class.java)
            }
        } catch (ex: Exception) {
            Logger.exception(ex)
            Logger.exception("error read purchase products")
            listOf()
        }
    }

    fun showPurchaseProduct(activity: Activity, billingService: BillingService, product: InAppProduct?,
                            body: ((BillingService.BillingResponseType) -> Unit)?) {
        getDeviceData(activity) { deviceData ->
            if (!AFNetworkManager.isOnline(activity)) {
                body?.invoke(BillingService.BillingResponseType.NOT_INTERNET)
                Logger.notify("return ${BillingService.BillingResponseType.NOT_INTERNET.name}")
                return@getDeviceData
            }
            if (product == null) {
                body?.invoke(BillingService.BillingResponseType.NOT_PRODUCT)
                Logger.notify("return ${BillingService.BillingResponseType.NOT_PRODUCT.name}")
                return@getDeviceData
            }
            val developerPayload = product.getNewDeveloperPayloadBase64(deviceData)
            Logger.notify("showFormPurchaseProduct developerPayload $developerPayload")
            try {
                val buyIntentBundle = billingService.inAppBillingService?.getBuyIntent(3,
                        activity.packageName, product.getSku(), product.type, developerPayload)
                val pendingIntent = buyIntentBundle?.getParcelable<PendingIntent>("BUY_INTENT")
                activity.startIntentSenderForResult(pendingIntent?.intentSender, StoreManager.Keys.REQUEST_CODE_BUY,
                        Intent(), 0, 0, 0)
                billingService.getStatus(body)
            } catch (ex: Exception) {
                Logger.exception(ex)
                Logger.notify("exception ${BillingService.BillingResponseType.NOT_AUTH.name}")
                body?.invoke(BillingService.BillingResponseType.NOT_AUTH)
            }
        }
    }
}