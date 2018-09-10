package com.appyfurious.afbilling.utils

import com.appyfurious.afbilling.product.InAppProduct
import com.appyfurious.log.Logger
import com.appyfurious.validation.ValidKeys
import com.appyfurious.validation.ValidationCallback
import com.appyfurious.validation.ValidationClient
import com.appyfurious.validation.body.ValidationBody
import java.util.*

class ValidationBilling(private val packageName: String) {

    companion object {
        fun checkInit() {
            if (ValidKeys.baseUrl.isEmpty() || ValidKeys.apiKey.isEmpty() || ValidKeys.secretKey.isEmpty())
                throw throw IllegalArgumentException("Invalid baseUrl or apiKey or secretKey")
        }
    }

    fun validateRequest(product: InAppProduct, listener: ValidationCallback.ValidationListener? = null) {
        validateRequest(product, product.getAdvertingId(), listener, null)
    }

    fun validateRequest(product: InAppProduct, advertingId: String, restoreListener: ValidationCallback.RestoreListener? = null) {
        validateRequest(product, advertingId, restoreListener)
    }

    private fun validateRequest(product: InAppProduct, advertingId: String, listener: ValidationCallback.ValidationListener? = null,
                                restoreListener: ValidationCallback.RestoreListener? = null) {
        val body = validationBody(product, advertingId)
        Logger.notify("validateRequest start")
        Logger.notify("validateRequest ValidationBody: $body")
        Logger.notify("baseUrl ${ValidKeys.baseUrl}, apiKey ${ValidKeys.apiKey}, secretKey ${ValidKeys.secretKey}")
        val service = ValidationClient.getValidationService(ValidKeys.secretKey, ValidKeys.baseUrl)
        val call = service.validate(ValidKeys.apiKey, body)
        val validationCallback = ValidationCallback(product, ValidKeys.secretKey, listener, restoreListener)
        call.enqueue(validationCallback)
        Logger.notify("validateRequest finish")
    }

    private fun validationBody(product: InAppProduct, advertingId: String) =
            ValidationBody(UUID.randomUUID().toString(), product.purchaseToken
                    ?: "product.purchaseToken", product.productId, ValidationBody.PRODUCT_TYPE,
                    packageName, product.developerPayload, product.getAppsflyerId(), advertingId)
}