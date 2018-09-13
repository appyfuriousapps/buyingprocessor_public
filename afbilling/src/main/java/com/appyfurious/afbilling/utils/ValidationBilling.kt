package com.appyfurious.afbilling.utils

import com.appyfurious.afbilling.product.MyProduct
import com.appyfurious.log.Logger
import com.appyfurious.validation.ValidKeys
import com.appyfurious.validation.ValidationCallback
import com.appyfurious.validation.ValidationClient
import com.appyfurious.validation.body.DeviceData
import com.appyfurious.validation.body.ValidationBody
import java.util.*

class ValidationBilling(private val packageName: String, private val deviceData: DeviceData) {

    companion object {
        fun checkInit() {
            if (ValidKeys.baseUrl.isEmpty() || ValidKeys.apiKey.isEmpty() || ValidKeys.secretKey.isEmpty())
                throw throw IllegalArgumentException("Invalid baseUrl or apiKey or secretKey")
        }
    }

    fun validateRequest(product: MyProduct, listener: ValidationCallback.ValidationListener? = null,
                        restoreListener: ValidationCallback.RestoreListener? = null) {
        val body = validationBody(product, deviceData)
        Logger.notify("validateRequest start")
        Logger.notify("validateRequest ValidationBody: $body")
        Logger.notify("baseUrl ${ValidKeys.baseUrl}, apiKey ${ValidKeys.apiKey}, secretKey ${ValidKeys.secretKey}")
        val service = ValidationClient.getValidationService(ValidKeys.secretKey, ValidKeys.baseUrl)
        val call = service.validate(ValidKeys.apiKey, body)
        val validationCallback = ValidationCallback(ValidKeys.secretKey, listener, restoreListener)
        call.enqueue(validationCallback)
        Logger.notify("validateRequest finish")
    }

    private fun validationBody(product: MyProduct, deviceData: DeviceData) =
            ValidationBody(UUID.randomUUID().toString(), product.purchaseToken
                    ?: "product.purchaseToken", product.productId, ValidationBody.PRODUCT_TYPE,
                    packageName, product.developerPayload, deviceData.appsflyerId, deviceData.idfa)
}