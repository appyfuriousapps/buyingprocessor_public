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
        private val context: Context,
        private val baseUrl: String,
        private val apiKey: String,
        private val secretKey: String,
        private val listener: BillingListener?,
        private val listSubs: List<ProductPreview>? = null) : BaseBilling {

    constructor(context: Context, baseUrl: String, apiKey: String, secretKey: String, isSubs: (Boolean) -> Unit)
            : this(context, baseUrl, apiKey, secretKey, null, null) {
        isSubsBody = isSubs
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

    private var isAuth = false
    private var isConnected = false

    private var products: List<InAppProduct>? = null
    private var inAppBillingService: IInAppBillingService? = null

    private var isSubsBody: ((Boolean) -> Unit)? = null

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                isConnected = true
                inAppBillingService = IInAppBillingService.Stub.asInterface(service)
                if (listSubs != null) {
                    products = getInAppPurchases(InAppProduct.SUBS, listSubs.map { it.id })
                    syncProducts(products, listSubs)
                    isAuth = products != null
                    listener?.billingConnectBody(products)
                } else {
                    isAuth = true
                    listener?.billingConnectBody(null)
                }
                isSubsStart()
            } catch (ex: Exception) {
                isSubsBody?.invoke(false)
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
        if (baseUrl.isEmpty() || apiKey.isEmpty() || secretKey.isEmpty())
            throw throw IllegalArgumentException("Invalid baseUrl or apiKey or secretKey")
        Thread {
            Logger.notify("init async start")
            try {
                val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
                serviceIntent.`package` = "com.android.vending"
                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            } catch (ex: Exception) {
                (context as? Activity)?.runOnUiThread {
                    isSubsBody?.invoke(false)
                }
                Logger.exception("init")
                error(ex)
            }
            Logger.notify("init async finish")
        }.start()
    }

    private fun isSubsStart() {
        isSubsBody?.let {
            isSubs { isSubs, _ ->
                isSubsBody?.invoke(isSubs)
            }
        }
    }

    private fun activity() = (context as Activity)

    private fun syncProducts(products: List<InAppProduct>?, productsPreview: List<ProductPreview>?) {
        productsPreview?.map { preview ->
            products?.filter { preview.id == it.productId }
                    ?.map {
                        preview.price = it.price!!
                        preview.appProduct = it
                    }
        }
    }

    private fun getInAppPurchases(type: String, productIds: List<String>): List<InAppProduct>? {
        val skuList = ArrayList(productIds)
        val query = Bundle()
        query.putStringArrayList("ITEM_ID_LIST", skuList)

        val skuDetails = inAppBillingService?.getSkuDetails(
                3, context.packageName, type, query)
        val responseList = skuDetails?.getStringArrayList("DETAILS_LIST")
        val gson = GsonBuilder().create()
        return responseList?.map {
            gson.fromJson(it, InAppProduct::class.java)
        }
    }

    override fun getServiceConnection() = serviceConnection
    override fun getProducts() = products
    override fun getInAppBillingService() = inAppBillingService

    override fun showFormPurchaseProduct(product: InAppProduct?, body: ((BillingResponseType) -> Unit)?) {
        if (product != null) {
            val buyIntentBundle = inAppBillingService?.getBuyIntent(3, context.packageName,
                    product.getSku(), product.getType(), "")
            val pendingIntent = buyIntentBundle?.getParcelable<PendingIntent>("BUY_INTENT")
            activity().startIntentSenderForResult(pendingIntent?.intentSender, REQUEST_CODE_BUY,
                    Intent(), 0, 0, 0)
        }
        if (isConnected && isAuth)
            body?.invoke(BillingResponseType.SUCCESS)
        else
            if (!isConnected)
                body?.invoke(BillingResponseType.NOT_CONNECTED)
            else
                if (!isAuth)
                    body?.invoke(BillingResponseType.NOT_AUTH)
    }

    override fun isSubs(body: (Boolean, InAppProduct?) -> Unit) {
        Logger.notify("restore init")
        readMyPurchases(InAppProduct.SUBS) {
            Logger.notify(it.joinToString(", ") { it.productId ?: "product_id" })
            var product: InAppProduct? = null
            val isSubs = it.isNotEmpty() && it.filter {
                it.purchaseState == Billing.PURCHASE_STATUS_PURCHASED
            }.map { product = it }.isNotEmpty()
            getAdvertingId { advertingId ->
                Logger.notify("advertingId: $advertingId, isSubs: $isSubs, product != null -> ${product != null}")
                if (product != null && isSubs) {
                    validateRequest(validationBody(product!!, advertingId), restoreListener =
                    object : ValidationCallback.ValidationRestoreListener {
                        override fun validationRestoreSuccess() {
                            Logger.notify("validationRestoreSuccess")
                            body(true, product)
                        }

                        override fun validationRestoreFailure(errorMessage: String) {
                            Logger.notify("validationRestoreFailure")
                            body(false, null)
                        }
                    })
                } else {
                    Logger.notify("ELSE validationRestoreFailure")
                    body(false, product)
                }
            }
        }
    }

    override fun isSubs(body: (Boolean) -> Unit) {
        val newBody = { isSubs: Boolean, _: InAppProduct? -> body(isSubs) }
        isSubs(newBody)
    }

    private fun readMyPurchases(type: String, body: (products: List<InAppProduct>) -> Unit) {
        Logger.notify("start readMyPurchases")
        var continuationToken: String? = null
        val gson = GsonBuilder().create()
        val myProducts = ArrayList<InAppProduct>()
        do {
            val result = inAppBillingService!!.getPurchases(
                    3, context.packageName, type, continuationToken)
            if (result.getInt("RESPONSE_CODE", -1) != 0) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?,
                                  listener: ValidationCallback.ValidationListener) {
        Logger.notify("start onActivityResult validate")
        if (requestCode == REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(RESPONSE_CODE, -1)
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                Logger.notify("onActivityResult validate BILLING_RESPONSE_RESULT_OK")
                isSubs { isSubs, product ->
                    getAdvertingId { advertingId ->
                        if (product != null && isSubs)
                            listener.onValidationShowProgress()
                        validateRequest(validationBody(product!!, advertingId), listener)
                    }
                }
            }
            if (responseCode == PURCHASE_STATUS_CANCELLED) {
                Logger.notify("onActivityResult validate PURCHASE_STATUS_CANCELLED")
                this.listener?.billingCanceled()
            }
        }
        Logger.notify("finish onActivityResult validate")
    }

    private fun validateRequest(body: ValidationBody, listener: ValidationCallback.ValidationListener? = null,
                                restoreListener: ValidationCallback.ValidationRestoreListener? = null) {
        Logger.notify("validateRequest start")
        Logger.notify("validateRequest ValidationBody: $body")
        Logger.notify("baseUrl $baseUrl, apiKey $apiKey, secretKey $secretKey")
        val service = ValidationClient.getValidationService(secretKey, baseUrl)
        val call = service.validate(apiKey, body)
        val validationCallback = ValidationCallback(secretKey, listener, restoreListener)
        call.enqueue(validationCallback)
        Logger.notify("validateRequest finish")
    }

    private fun validationBody(product: InAppProduct, adInfoId: String) =
            ValidationBody(UUID.randomUUID().toString(), product.purchaseToken
                    ?: "product.purchaseToken",
                    product.productId, ValidationBody.PRODUCT_TYPE, context.packageName,
                    product.developerPayload, AppsFlyerLib.getInstance().getAppsFlyerUID(context),
                    adInfoId)

    private fun getAdvertingId(success: (String) -> Unit) {
        AdvertisingIdClient.getAdvertisingId(context, object : AdvertisingIdClient.Listener {
            private val default = "advertingId"
            override fun onAdvertisingIdClientFinish(adInfo: AdvertisingIdClient.AdInfo?) {
                Logger.notify("onAdvertisingIdClientFinish ${adInfo?.id}")
                success(adInfo?.id ?: default)
            }

            override fun onAdvertisingIdClientFail(exception: Exception) {
                Logger.notify("onAdvertisingIdClientFail advertingId")
                success(default)
            }
        })
    }

    enum class BillingResponseType {
        SUCCESS, NOT_CONNECTED, NOT_AUTH
    }

    interface BillingListener {
        fun billingConnectBody(products: (List<InAppProduct>)?)
        fun billingCanceled()
    }
}
