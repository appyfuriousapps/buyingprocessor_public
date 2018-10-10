package com.appyfurious.afbilling

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.afbilling.product.MyProduct
import com.appyfurious.afbilling.product.ProductsManager
import com.appyfurious.afbilling.service.BillingService
import com.appyfurious.afbilling.utils.ValidationBilling
import com.appyfurious.analytics.Events
import com.appyfurious.log.Logger
import com.appyfurious.utils.AFNetworkManager
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

    private var inAppProductsId = listOf<String>()
    var myProducts = listOf<MyProduct>()
        private set
    var inAppProducts = listOf<InAppProduct>()
        private set

    val isSubsData = MutableLiveData<Boolean>()

    fun getInAppProduct(productId: String) = inAppProducts.firstOrNull { it.productId == productId }

    fun init(application: Application, baseUrl: String, apiKey: String, secretKey: String) {
        this.application = application
        billingService = BillingService(application)
        ProcessLifecycleOwner.get().lifecycle.addObserver(listener)
        ValidKeys.init(baseUrl, apiKey, secretKey)
        productManager = ProductsManager(application)
        Logger.notify("success init StoreManager $baseUrl $apiKey $secretKey")
    }

    fun updateProducts(inAppProductsId: List<String>) {
        this.inAppProductsId = inAppProductsId
        if (billingService.isConnected) {
            Logger.notify("billingService.isConnected == true")
            inAppProducts = productManager.getInAppPurchases(billingService.inAppBillingService, InAppProduct.SUBS, this.inAppProductsId)
        } else {
            Logger.notify("billingService.isConnected == false")
            billingService.addConnectedListener {
                Logger.notify("billingService.isConnected == false, addConnectedListener")
                inAppProducts = productManager.getInAppPurchases(billingService.inAppBillingService, InAppProduct.SUBS, this.inAppProductsId)
            }
        }
    }

    fun showPurchaseProduct(activity: Activity, product: InAppProduct?, body: ((BillingService.BillingResponseType) -> Unit)?) {
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

    fun isSubs(body: (Boolean) -> Unit) {
        if (billingService.isConnected) {
            isSubs(null) { _, isSubs -> body(isSubs) }
        } else {
            billingService.addConnectedListener {
                isSubs(null) { _, isSubs -> body(isSubs) }
            }
        }
    }

    private val listener = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onMoveToForeground() {
            Logger.notify("onMoveToForeground")
            billingService = BillingService(application)
            if (billingService.isConnected) {
                isSubs(null) { product, isSubs ->
                    Logger.notify("onMoveToForeground  isSubs: $isSubs, isActive: ${product?.isActive()}, ${product?.productId}")
                }
            } else {
                billingService.addConnectedListener { service ->
                    inAppProducts = productManager.getInAppPurchases(service, InAppProduct.SUBS, inAppProductsId)
                    Logger.notify("success service connection, productsId: ${inAppProductsId.joinToString(", ")}")
                    isSubs(null, null)
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            Logger.notify("onMoveToBackground")
            billingService.unbind(application)
        }
    }

    private fun isSubs(listener: ValidationCallback.ValidationListener?, body: ((MyProduct?, Boolean) -> Unit)?) {
        productManager.readMyPurchases(billingService.inAppBillingService, InAppProduct.SUBS) { products ->
            myProducts = products
            val product = myProducts.firstOrNull { it.isActive() }
            val isSubs = product?.isActive() == true
            if (isSubs && product != null) {
                Logger.notify("preValidation isSubs: $isSubs, ${product.productId}")
                validation(application, listener, product) { validationIsSubs ->
                    Logger.notify("postValidation isSubs: $validationIsSubs")
                    body?.invoke(product, validationIsSubs)
                }
            } else {
                Logger.notify("product == null isSubs: $isSubs")
                isSubsData.value = isSubs
                body?.invoke(product, isSubs)
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
            isSubsData.value = true
            body(true)
        }

        override fun validationRestoreFailure(errorMessage: String) {
            Logger.notify("validationRestoreFailure")
            isSubsData.value = false
            body(false)
        }
    }
}