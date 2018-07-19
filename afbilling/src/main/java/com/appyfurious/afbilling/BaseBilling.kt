package com.appyfurious.afbilling

import android.content.Intent
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.validation.ValidationCallback

interface BaseBilling {
    fun getProducts(): List<InAppProduct>?
    fun getInAppBillingService(): IInAppBillingService?
    fun isSubs(body: (Boolean) -> Unit)
    fun isSubs(body: (Boolean, InAppProduct?) -> Unit)
    fun showFormPurchaseProduct(product: InAppProduct, developerPayload: String)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: ValidationCallback.ValidationListener)
}