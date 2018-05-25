package com.appyfurious.afbilling

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.android.vending.billing.IInAppBillingService
import com.google.gson.GsonBuilder

open class Billing(
        private val contextUI: Context,
        private val activity: Activity,
        private val errorServiceConnection: (_: Exception) -> Unit,
        private val listSubs: List<ProductPreview>? = null,
        private val connectBody: ((products: List<InAppProduct>?) -> Unit)? = null) {

    companion object {
        const val REQUEST_CODE_BUY = 1234
        const val BILLING_RESPONSE_RESULT_OK = 0
        const val BILLING_RESPONSE_RESULT_USER_CANCELED = 1
        const val BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2
        const val BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3
        const val BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4
        const val BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5
        const val BILLING_RESPONSE_RESULT_ERROR = 6
        const val BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7
        const val BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8
        const val PURCHASE_STATUS_PURCHASED = 0
        const val PURCHASE_STATUS_CANCELLED = 1
        const val PURCHASE_STATUS_REFUNDED = 2
        const val RESPONSE_CODE = "RESPONSE_CODE"
    }

    var products: List<InAppProduct>? = null
        protected set
    var inAppBillingService: IInAppBillingService? = null
        protected set

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service)
                if (listSubs != null) {
                    products = getInAppPurchases(InAppProduct.SUBS, listSubs.map { it.id })
                    syncProducts(products, listSubs)
                }
                connectBody?.invoke(products)
            } catch (ex: Exception) {
                error(ex)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            inAppBillingService = null
        }
    }

    init {
        Thread {
            try {
                val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
                serviceIntent.`package` = "com.android.vending"
                contextUI.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } catch (ex: Exception) {
                error(ex)
            }
        }.start()
    }

    private fun syncProducts(products: List<InAppProduct>?, productsPreview: List<ProductPreview>?) {
        productsPreview?.map { preview ->
            products?.filter { preview.id == it.productId }
                    ?.map {
                        preview.price = it.price!!
                        preview.appProduct = it
                    }
        }
    }

    fun getInAppPurchases(type: String, productIds: List<String>): List<InAppProduct>? {
        val skuList = ArrayList(productIds)
        val query = Bundle()
        query.putStringArrayList("ITEM_ID_LIST", skuList)
        val skuDetails = inAppBillingService!!.getSkuDetails(
                3, contextUI.packageName, type, query)
        val responseList = skuDetails?.getStringArrayList("DETAILS_LIST")
        val gson = GsonBuilder().create()
        return responseList?.map {
            gson.fromJson(it, InAppProduct::class.java)
        }
    }

    fun showFormPurchaseProduct(product: InAppProduct, developerPayload: String = "12345") {
        val buyIntentBundle = inAppBillingService!!.getBuyIntent(3, contextUI.packageName,
                product.getSku(), product.getType(), developerPayload)
        val pendingIntent = buyIntentBundle.getParcelable<PendingIntent>("BUY_INTENT")
        activity.startIntentSenderForResult(pendingIntent!!.intentSender, REQUEST_CODE_BUY, Intent(), 0, 0, 0)
    }

    fun isSubs(body: (Boolean) -> Unit) {
        readMyPurchases(InAppProduct.SUBS) {
            val isSubs = it.isNotEmpty() && it.map {
                it.purchaseState == Billing.PURCHASE_STATUS_PURCHASED
            }.isNotEmpty()
            body(isSubs)
        }
    }

    @Deprecated("use isSubs method")
    fun readMyPurchases(type: String, body: (products: List<InAppProduct>) -> Unit) {
        var continuationToken: String? = null
        val gson = GsonBuilder().create()
        val myProduct = ArrayList<InAppProduct>()
        do {
            val result = inAppBillingService!!.getPurchases(
                    3, contextUI.packageName, type, continuationToken)
            if (result.getInt("RESPONSE_CODE", -1) != 0) {
                throw Exception("Invalid response code")
            }
            val responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
            val serverProducts = responseList.map {
                gson.fromJson(it, InAppProduct::class.java)
            }
            myProduct.addAll(serverProducts)
            continuationToken = result.getString("INAPP_CONTINUATION_TOKEN")
        } while (continuationToken != null)
        body(myProduct)
    }
}
