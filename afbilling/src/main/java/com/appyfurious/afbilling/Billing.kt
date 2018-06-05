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
import com.appsflyer.AppsFlyerLib
import com.appyfurious.log.Logger
import com.appyfurious.validation.ValidationCallback
import com.appyfurious.validation.ValidationClient
import com.appyfurious.validation.body.ValidationBody
import com.appyfurious.validation.utils.AdvertisingIdClient
import com.google.gson.GsonBuilder
import java.util.*

open class Billing(
        private val listener: BillingListener,
        private val listSubs: List<ProductPreview>? = null) {

    interface BillingListener {
        fun billingContext(): Context?
        fun connectBody(products: (List<InAppProduct>?))
        fun purchases()
        fun canceled()
    }

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

    //public
    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service)
                if (listSubs != null) {
                    products = getInAppPurchases(InAppProduct.SUBS, listSubs.map { it.id })
                    syncProducts(products, listSubs)
                }
                listener.connectBody(products)
            } catch (ex: Exception) {
                Logger.notify("serviceConnection")
                ex.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            inAppBillingService = null
            Logger.notify("onServiceDisconnected inAppBillingService = null")
        }
    }

    init {
        Thread {
            Logger.notify("init async start")
            try {
                val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
                serviceIntent.`package` = "com.android.vending"
                listener.billingContext()?.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } catch (ex: Exception) {
                Logger.exception("init")
                error(ex)
            }
            Logger.notify("init async finish")
        }.start()
    }

    private fun activity() = (listener.billingContext() as Activity)

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
                3, listener.billingContext()?.packageName, type, query)
        val responseList = skuDetails?.getStringArrayList("DETAILS_LIST")
        val gson = GsonBuilder().create()
        return responseList?.map {
            gson.fromJson(it, InAppProduct::class.java)
        }
    }

    fun showFormPurchaseProduct(product: InAppProduct, developerPayload: String = "12345") {
        val buyIntentBundle = inAppBillingService!!.getBuyIntent(3, listener.billingContext()?.packageName,
                product.getSku(), product.getType(), developerPayload)
        val pendingIntent = buyIntentBundle.getParcelable<PendingIntent>("BUY_INTENT")
        activity().startIntentSenderForResult(pendingIntent!!.intentSender, REQUEST_CODE_BUY,
                Intent(), 0, 0, 0)
    }

    fun isSubs(body: (Boolean) -> Unit) {
        readMyPurchases(InAppProduct.SUBS) {
            val isSubs = it.isNotEmpty() && it.map {
                it.purchaseState == Billing.PURCHASE_STATUS_PURCHASED
            }.isNotEmpty()
            body(isSubs)
        }
    }

    fun isSubs(body: (Boolean, InAppProduct?) -> Unit) {
        readMyPurchases(InAppProduct.SUBS) {
            var product: InAppProduct? = null
            val isSubs = it.isNotEmpty() && it.filter {
                it.purchaseState == Billing.PURCHASE_STATUS_PURCHASED
            }.map { product = it }.isNotEmpty()
            body(isSubs, product)
        }
    }

    @Deprecated("use isSubs method")
    fun readMyPurchases(type: String, body: (products: List<InAppProduct>) -> Unit) {
        Logger.notify("start readMyPurchases")
        var continuationToken: String? = null
        val gson = GsonBuilder().create()
        val myProduct = ArrayList<InAppProduct>()
        do {
            val result = inAppBillingService!!.getPurchases(
                    3, listener.billingContext()?.packageName, type, continuationToken)
            if (result.getInt("RESPONSE_CODE", -1) != 0) {
                throw Exception("Invalid response code")
            }
            val responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
            val serverProducts = responseList.map {
                Logger.notify("readMyPurchases $it")
                gson.fromJson(it, InAppProduct::class.java)
            }
            myProduct.addAll(serverProducts)
            continuationToken = result.getString("INAPP_CONTINUATION_TOKEN")
        } while (continuationToken != null)
        body(myProduct)
        Logger.notify("finish readMyPurchases")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.notify("start onActivityResult not validate")
        if (requestCode == REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(RESPONSE_CODE, -1)
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                Logger.notify("onActivityResult not validate BILLING_RESPONSE_RESULT_OK")
                listener.purchases()
            }
            if (responseCode == PURCHASE_STATUS_CANCELLED) {
                Logger.notify("onActivityResult not validate PURCHASE_STATUS_CANCELLED")
                listener.canceled()
            }
        }
        Logger.notify("finish onActivityResult not validate")
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, serverValidateUrl: String,
                         apiKey: String, secretKey: String, listener: ValidationCallback.ValidationListener) {
        Logger.notify("start onActivityResult validate")
        if (requestCode == REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(RESPONSE_CODE, -1)
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                Logger.notify("onActivityResult validate BILLING_RESPONSE_RESULT_OK")
                isSubs { isSubs, product ->
                    getAdvertingId { advertingId ->
                        if (product != null && isSubs)
                            listener.showProgress()
                            validateRequest(validationBody(product!!, advertingId),
                                    serverValidateUrl, apiKey, secretKey, listener)
                    }
                }
            }
            if (responseCode == PURCHASE_STATUS_CANCELLED) {
                Logger.notify("onActivityResult validate PURCHASE_STATUS_CANCELLED")
                this.listener.canceled()
            }
        }
        Logger.notify("finish onActivityResult validate")
    }

    private fun validateRequest(body: ValidationBody, baseUrl: String, apiKey: String,
                                secretKey: String, listener: ValidationCallback.ValidationListener) {
        Logger.notify("validateRequest start")
        Logger.notify("validateRequest ValidationBody: $body")
        Logger.notify("baseUrl $baseUrl, apiKey $apiKey, secretKey $secretKey")
        val service = ValidationClient.getValidationService(secretKey, baseUrl)
        val call = service.validate(apiKey, body)
        val validationCallback = ValidationCallback(secretKey, listener)
        call.enqueue(validationCallback)
        Logger.notify("validateRequest finish")
    }

    private fun validationBody(product: InAppProduct, adInfoId: String) =
            ValidationBody(UUID.randomUUID().toString(), product.purchaseToken
                    ?: "product.purchaseToken",
                    product.productId, ValidationBody.PRODUCT_TYPE, listener.billingContext()?.packageName,
                    product.developerPayload, AppsFlyerLib.getInstance().getAppsFlyerUID(listener.billingContext()),
                    adInfoId)

    private fun getAdvertingId(success: (String) -> Unit) {
        AdvertisingIdClient.getAdvertisingId(listener.billingContext(), object : AdvertisingIdClient.Listener {
            override fun onAdvertisingIdClientFinish(adInfo: AdvertisingIdClient.AdInfo) {
                Logger.notify("onAdvertisingIdClientFinish ${adInfo.id}")
                success(adInfo.id)
            }

            override fun onAdvertisingIdClientFail(exception: Exception) {
                Logger.notify("onAdvertisingIdClientFail advertingId")
                success("advertingId")
            }
        })
    }
}
