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

    private lateinit var application: Application
    private lateinit var productManager: ProductsManager
    private lateinit var billingService: BillingService

    lateinit var myProducts: List<MyProduct>
        private set
    lateinit var inAppProducts: List<InAppProduct>
        private set
    val isSubsData = MutableLiveData<Boolean>()

    fun init(application: Application, inAppProductsId: List<String>, baseUrl: String, apiKey: String, secretKey: String) {
        this.application = application
        ProcessLifecycleOwner.get().lifecycle.addObserver(listener)
        ValidKeys.init(baseUrl, apiKey, secretKey)
        productManager = ProductsManager(application)
        billingService = BillingService { service ->
            inAppProducts = productManager.getInAppPurchases(service, InAppProduct.SUBS, inAppProductsId)
            Logger.notify("success service connection, productsId: ${inAppProductsId.joinToString(", ")}")
        }
        Logger.notify("success init StoreManager $baseUrl $apiKey $secretKey")
    }

    fun showPurchaseProduct(activity: Activity, product: InAppProduct, body: ((BillingService.BillingResponseType) -> Unit)?) {
        productManager.showPurchaseProduct(activity, billingService, product, body)
    }

    fun onActivityResult(requestCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener) {
        Logger.notify("start onActivityResult validate")
        if (requestCode == Billing.REQUEST_CODE_BUY) {
            val responseCode = data?.getIntExtra(Billing.RESPONSE_CODE, -1)
            if (responseCode == Billing.BILLING_RESPONSE_RESULT_OK) {
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
            if (responseCode == Billing.PURCHASE_STATUS_CANCELLED) {
                Logger.notify("onActivityResult validate PURCHASE_STATUS_CANCELLED")
            }
        }
        Logger.notify("finish onActivityResult validate")
    }

    fun isSubs(body: (Boolean) -> Unit) {
        isSubs(null) { _, isSubs ->
            body(isSubs)
        }
    }

    private fun isSubs(listener: ValidationCallback.ValidationListener?, body: (MyProduct?, Boolean) -> Unit) {
        productManager.readMyPurchases(billingService.inAppBillingService, InAppProduct.SUBS) { products ->
            myProducts = products
            val product = myProducts.firstOrNull { it.isActive() }
            val isSubs = product?.isActive() == true
            isSubsData.value = isSubs
            if (isSubs && product != null) {
                validation(application, listener, product) { body(product, it) }
            } else {
                body(product, false)
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
            isSubs { isSubs ->
                isSubsData.value = isSubs
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            Logger.notify("onMoveToBackground")
        }
    }
}