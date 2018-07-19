package com.appyfurious.afbilling

import android.content.Intent
import android.content.ServiceConnection
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.validation.ValidationCallback

interface BaseBilling {
    fun getProducts(): List<InAppProduct>?
    fun getInAppBillingService(): IInAppBillingService?
    fun getServiceConnection(): ServiceConnection

    fun isSubs(body: (Boolean) -> Unit)
    fun isSubs(body: (Boolean, InAppProduct?) -> Unit)
    fun showFormPurchaseProduct(product: InAppProduct, developerPayload: String)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener)
}