package com.appyfurious.afbilling

import android.app.Activity
import android.app.PendingIntent
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.afbilling.product.ProductsManager
import com.appyfurious.afbilling.product.MyProduct
import com.appyfurious.afbilling.product.ProductPreview
import com.appyfurious.afbilling.utils.ValidationBilling
import com.appyfurious.analytics.Events
import com.appyfurious.log.Logger
import com.appyfurious.validation.ValidationCallback

class Billing(
        private val context: Context,
        private val listener: BillingListener?,
        private val listSubs: List<ProductPreview>? = null) : BaseBilling, LifecycleObserver {

    companion object {
        const val REQUEST_CODE_BUY = 1234
        const val BILLING_RESPONSE_RESULT_OK = 0
        const val PURCHASE_STATUS_PURCHASED = 0
        const val PURCHASE_STATUS_CANCELLED = 1
        const val RESPONSE_CODE = "RESPONSE_CODE"
    }

    private val lifecycle: Lifecycle
    private var isAuth = false
    private var isConnected = false
    private var products: List<InAppProduct>? = null
    private var inAppBillingService: IInAppBillingService? = null
    private var isSubsBody: ((Boolean) -> Unit)? = null

    private val productManager = ProductsManager(context)
    private lateinit var selectedInAppProduct: InAppProduct

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            isConnected = true
            try {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service)
                if (listSubs != null) {
                    products = productManager.getInAppPurchases(inAppBillingService, InAppProduct.SUBS, listSubs.map { it.id })
                    productManager.syncProducts(products, listSubs)
                    isAuth = products != null
                    listener?.billingConnectBody(products)
                } else {
                    isAuth = true
                    listener?.billingConnectBody(null)
                }
                isSubsBody?.let {
                    isSubs(null) { isSubs, _ ->
                        isSubsBody?.invoke(isSubs)
                    }
                }
            } catch (ex: Exception) {
                isSubsBody?.invoke(false)
                Logger.notify("serviceConnection")
                ex.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isConnected = false
            inAppBillingService = null
            Logger.notify("onServiceDisconnected inAppBillingService = null")
        }
    }

    init {
        ValidationBilling.checkInit()
        lifecycle = (context as FragmentActivity).lifecycle
        lifecycle.addObserver(this)
        Logger.notify("base constructor init")
    }

    constructor(context: Context, isSubs: (Boolean) -> Unit) : this(context, null, null) {
        ValidationBilling.checkInit()
        isSubsBody = isSubs
        Logger.notify("isSubs constructor init")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        Logger.notify("onResume connected")
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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        Logger.notify("onPause disconnected")
        if (isConnected)
            activity().unbindService(serviceConnection)
        isConnected = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        lifecycle.removeObserver(this)
    }

    private fun activity() = (context as Activity)

    override fun getServiceConnection() = serviceConnection
    override fun getProducts() = products
    override fun getInAppBillingService() = inAppBillingService

    override fun showFormPurchaseProduct(product: InAppProduct?, body: ((BillingResponseType) -> Unit)?) {
        if (product != null) {
            productManager.getDeviceData(context) { deviceData ->
                selectedInAppProduct = product
                val developerPayload = product.getNewDeveloperPayloadBase64(deviceData)
                Logger.notify("showFormPurchaseProduct developerPayload $developerPayload")
                val buyIntentBundle = inAppBillingService?.getBuyIntent(3, context.packageName,
                        product.getSku(), product.type, developerPayload)
                val pendingIntent = buyIntentBundle?.getParcelable<PendingIntent>("BUY_INTENT")
                activity().startIntentSenderForResult(pendingIntent?.intentSender, REQUEST_CODE_BUY,
                        Intent(), 0, 0, 0)
            }
        }
        if (isConnected && isAuth) {
            body?.invoke(BillingResponseType.SUCCESS)
        } else
            if (!isConnected)
                body?.invoke(BillingResponseType.NOT_CONNECTED)
            else
                if (!isAuth)
                    body?.invoke(BillingResponseType.NOT_AUTH)
    }

    override fun isSubs(listener: ValidationCallback.ValidationListener?, body: (Boolean, MyProduct?) -> Unit) {
        Logger.notify("restore init")
        productManager.readMyPurchases(inAppBillingService, InAppProduct.SUBS) { products ->
            readMyPurchasesResult(listener, body, products)
        }
    }

    private val readMyPurchasesResult = { listener: ValidationCallback.ValidationListener?,
                                          body: (Boolean, MyProduct?) -> Unit, it: List<MyProduct> ->
        Logger.notify(it.joinToString(", ") { it.productId ?: "product_id" })
        var product: MyProduct? = null
        val isSubs = it.isNotEmpty() && it.filter {
            it.purchaseState == Billing.PURCHASE_STATUS_PURCHASED
        }.map { product = it }.isNotEmpty()
        Logger.notify("advertingId: DEPRICATE(advertingId)," + "isSubs: $isSubs, product != null -> ${product != null}")
        if (product != null && isSubs) {
            val restore = object : ValidationCallback.RestoreListener {
                override fun validationRestoreSuccess() {
                    Logger.notify("validationRestoreSuccess")
                    body(true, product)
                }

                override fun validationRestoreFailure(errorMessage: String) {
                    Logger.notify("validationRestoreFailure")
                    body(false, null)
                }
            }
            productManager.getDeviceData(context) { deviceData ->
                ValidationBilling(context.packageName, deviceData).validateRequest(product!!, listener, restore)
            }
        } else {
            Logger.notify("ELSE validationRestoreFailure")
            body(false, product)
        }
    }

    override fun isSubs(body: (Boolean) -> Unit) {
        val newBody = { isSubs: Boolean, _: MyProduct? -> body(isSubs) }
        isSubs(null, newBody)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener) {
        Logger.notify("start onActivityResult validate")
        if (requestCode == REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(RESPONSE_CODE, -1)
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                Logger.notify("onActivityResult validate BILLING_RESPONSE_RESULT_OK")
                listener.onValidationShowProgress()
                isSubs(listener) { isSubs, product ->
                    if (isSubs && product != null) {
                        Events.logPurchaseEvents(context, selectedInAppProduct)
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

    enum class BillingResponseType {
        SUCCESS, NOT_CONNECTED, NOT_AUTH
    }

    interface BillingListener {
        fun billingConnectBody(products: (List<InAppProduct>)?)
        fun billingCanceled()
    }
}
