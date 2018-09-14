package com.appyfurious.afbilling

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.afbilling.product.MyProduct
import com.appyfurious.afbilling.product.ProductsManager
import com.appyfurious.afbilling.service.BillingService
import com.appyfurious.afbilling.utils.ValidationBilling
import com.appyfurious.analytics.Events
import com.appyfurious.log.Logger
import com.appyfurious.validation.ValidKeys
import com.appyfurious.validation.ValidationCallback

object StoreManager {

    object Keys {
        const val REQUEST_CODE_BUY = 1234
        const val BILLING_RESPONSE_RESULT_OK = 0
        const val PURCHASE_STATUS_PURCHASED = 0
        const val PURCHASE_STATUS_CANCELLED = 1
        const val RESPONSE_CODE = "RESPONSE_CODE"
    }

    private lateinit var application: Application
    private lateinit var productManager: ProductsManager
    private lateinit var billingService: BillingService
    private var isLazyInit = false

    lateinit var myProducts: List<MyProduct>
        private set
    lateinit var inAppProducts: List<InAppProduct>
        private set

    val isSubsData = MutableLiveData<Boolean>()

    fun getInAppProduct(productId: String) = inAppProducts.firstOrNull { it.productId == productId }

    fun init(application: Application, inAppProductsId: List<String>, baseUrl: String, apiKey: String, secretKey: String) {
        this.application = application
        ProcessLifecycleOwner.get().lifecycle.addObserver(listener)
        ValidKeys.init(baseUrl, apiKey, secretKey)
        productManager = ProductsManager(application)
        billingService = BillingService(application) { service ->
            inAppProducts = productManager.getInAppPurchases(service, InAppProduct.SUBS, inAppProductsId)
            Logger.notify("success service connection, productsId: ${inAppProductsId.joinToString(", ")}")
            if (isLazyInit) {
                isLazyInit = false
                isSubs(null, null)
            }
        }
        Logger.notify("success init StoreManager $baseUrl $apiKey $secretKey")
    }

    fun showPurchaseProduct(activity: Activity, product: InAppProduct, body: ((BillingService.BillingResponseType) -> Unit)?) {
        productManager.showPurchaseProduct(activity, billingService, product, body)
    }

    fun onActivityResult(requestCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener) {
        Logger.notify("start onActivityResult validate")
        if (requestCode == Keys.REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(Keys.RESPONSE_CODE, -1)
            if (responseCode == Keys.BILLING_RESPONSE_RESULT_OK) {
                Logger.notify("onActivityResult validate BILLING_RESPONSE_RESULT_OK")
                listener.onValidationShowProgress()
                isSubs(listener) { product, isSubs ->
                    if (isSubs && product != null) {
                        val inAppProduct = inAppProducts.firstOrNull { it.productId == product.productId }
                        inAppProduct?.let {
                            Events.logPurchaseEvents(application, it)
                        }
                    }
                }
            }
            if (responseCode == Keys.PURCHASE_STATUS_CANCELLED) {
                Logger.notify("onActivityResult validate PURCHASE_STATUS_CANCELLED")
            }
        }
        Logger.notify("finish onActivityResult validate")
    }

    fun isSubs(owner: LifecycleOwner, body: (Boolean) -> Unit) {
        if (billingService.isConnected) {
            isSubs(null) { _, isSubs -> body(isSubs) }
        } else {
            billingService.isConnectedInit.observe(owner, Observer {
                isSubs(null) { _, isSubs -> body(isSubs) }
            })
        }
    }

    private fun isSubs(listener: ValidationCallback.ValidationListener?, body: ((MyProduct?, Boolean) -> Unit)?) {
        productManager.readMyPurchases(billingService.inAppBillingService, InAppProduct.SUBS) { products ->
            myProducts = products
            val product = myProducts.firstOrNull { it.isActive() }
            val isSubs = product?.isActive() == true
            if (isSubs && product != null) {
                validation(application, listener, product) {
                    isSubsData.value = isSubs
                    body?.invoke(product, it)
                }
            } else {
                isSubsData.value = isSubs
                body?.invoke(product, false)
            }
        }
    }

    private fun validation(context: Context, listener: ValidationCallback.ValidationListener?,
                           product: MyProduct, body: (Boolean) -> Unit) {
        productManager.getDeviceData(context) { deviceData ->
            ValidationBilling(context.packageName, deviceData).validateRequest(product, listener, restore(body))
        }
    }

    private fun restore(body: (Boolean) -> Unit) = object : ValidationCallback.RestoreListener {
        override fun validationRestoreSuccess() {
            Logger.notify("validationRestoreSuccess")
            body(true)
        }

        override fun validationRestoreFailure(errorMessage: String) {
            Logger.notify("validationRestoreFailure")
            body(false)
        }
    }

    private val listener = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onMoveToForeground() {
            Logger.notify("onMoveToForeground")
            if (billingService.isConnected) {
                isSubs(null) { product, isSubs ->
                    isSubsData.value = isSubs
                    Logger.notify("onMoveToForeground  isSubs: $isSubs, isActive: ${product?.isActive()}, ${product?.productId}")
                }
            } else {
                isLazyInit = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            Logger.notify("onMoveToBackground")
        }
    }
}