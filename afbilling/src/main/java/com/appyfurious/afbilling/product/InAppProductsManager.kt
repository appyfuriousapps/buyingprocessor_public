package com.appyfurious.afbilling.product

import android.content.Context
import android.os.Bundle
import com.android.vending.billing.IInAppBillingService
import com.appsflyer.AppsFlyerLib
import com.appyfurious.afbilling.utils.Adverting
import com.appyfurious.log.Logger
import com.appyfurious.validation.body.DeviceData
import com.google.gson.GsonBuilder
import java.util.*

class InAppProductsManager(context: Context) {

    private val packageName = context.packageName

    val developerPayload = DeviceData(AppsFlyerLib.getInstance().getAppsFlyerUID(context)!!, "")

    init {
        Adverting(context) {
            developerPayload.idfa = it
            Logger.notify("advertingId: ${developerPayload.idfa}")
        }
    }

    fun syncProducts(products: List<InAppProduct>?, productsPreview: List<ProductPreview>?) {
        productsPreview?.map { preview ->
            products?.filter { preview.id == it.productId }
                    ?.map { product ->
                        preview.price = product.price!!
                        preview.appProduct = product
                    }
        }
    }

    fun readMyPurchases(service: IInAppBillingService?, type: String,
                        body: (products: List<InAppProduct>) -> Unit) {
        Logger.notify("start readMyPurchases")
        var continuationToken: String? = null
        val gson = GsonBuilder().create()
        val myProducts = ArrayList<InAppProduct>()
        do {
            val result = service?.getPurchases(3, packageName, type, continuationToken)
            if (result?.getInt("RESPONSE_CODE", -1) != 0) {
                throw Exception("Invalid response code")
            }
            val responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
            val serverProducts = responseList.map {
                Logger.notify("readMyPurchases $it")
                gson.fromJson(it, InAppProduct::class.java)
            }
            myProducts.addAll(serverProducts)
            continuationToken = result.getString("INAPP_CONTINUATION_TOKEN")
        } while (continuationToken != null)
        body(myProducts)
        Logger.notify("finish readMyPurchases")
    }

    fun getInAppPurchases(service: IInAppBillingService?, type: String, productIds: List<String>): List<InAppProduct>? {
        val skuList = ArrayList(productIds)
        val query = Bundle()
        query.putStringArrayList("ITEM_ID_LIST", skuList)

        val skuDetails = service?.getSkuDetails(3, packageName, type, query)
        val responseList = skuDetails?.getStringArrayList("DETAILS_LIST")
        val gson = GsonBuilder().create()
        return responseList?.map {
            gson.fromJson(it, InAppProduct::class.java)
        }
    }
}