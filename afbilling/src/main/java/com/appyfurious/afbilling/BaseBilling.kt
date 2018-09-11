package com.appyfurious.afbilling

import android.content.Intent
import android.content.ServiceConnection
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.validation.ValidationCallback

interface BaseBilling {
    fun getProducts(): List<InAppProduct>?
    fun getInAppBillingService(): IInAppBillingService?
    fun getServiceConnection(): ServiceConnection

    fun isSubs(body: (Boolean) -> Unit)
    fun isSubs(listener: ValidationCallback.ValidationListener?, body: (Boolean, InAppProduct?) -> Unit)
    fun showFormPurchaseProduct(product: InAppProduct?, body: ((Billing.BillingResponseType) -> Unit)?)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener)
}