package com.appyfurious.afbilling.product

import android.util.Base64
import com.appyfurious.log.Logger
import com.appyfurious.validation.body.DeviceData
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat

open class InAppProduct {

    companion object {
        const val SUBS = "subs"
        const val INAPP = "inapp"
    }

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("packageName")
    @Expose
    var packageName: String? = null

    @SerializedName("productId")
    @Expose
    var productId: String? = null

    @SerializedName("purchaseTime")
    @Expose
    var purchaseTime: Long? = null
    @SerializedName("purchaseState")
    @Expose
    var purchaseState: Int? = null

    @SerializedName("developerPayload")
    @Expose
    var developerPayload: String? = null

    @SerializedName("purchaseToken")
    @Expose
    var purchaseToken: String? = null

    @SerializedName("autoRenewing")
    @Expose
    var autoRenewing: Boolean? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("price_amount_micros")
    @Expose
    var priceAmountMicros: Int? = null

    @SerializedName("price_currency_code")
    @Expose
    var priceCurrencyCode: String? = null

    @SerializedName("subscriptionPeriod")
    @Expose
    var subscriptionPeriod: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    fun getDeveloperPayloadBase64(developerPayload: DeviceData): String {
        val gson = GsonBuilder().create()
        val obj = gson.toJson(developerPayload)
        val result = String(Base64.encode(obj.toByteArray(), Base64.DEFAULT))
        Logger.notify("setDeveloperPayload $obj")
        Logger.notify("setDeveloperPayload $result")
        return result.replace("\\n", "").replace("\n", "")
    }

    fun getSku(): String? = productId

    fun getType() = SUBS

    fun getPriceParse(): Double {
        var textResult = price?.replace("-", "")
                ?.replace("$", "")
                ?.replace(" ", "")
                ?.replace(",", ".")
        if (textResult != null && textResult.find { it == '.' } == null)
            textResult += ".0"
        return try {
            val formatter = DecimalFormat("0.00")
            val result = formatter.parse(textResult)
            result.toDouble()
        } catch (ex: Exception) {
            1.0
        }
    }

    fun set(product: InAppProduct) {
        orderId = product.orderId
        packageName = product.packageName
        productId = product.productId
        purchaseTime = product.purchaseTime
        purchaseState = product.purchaseState
        developerPayload = product.developerPayload
        purchaseToken = product.purchaseToken
        autoRenewing = product.autoRenewing
        price = product.price
        priceAmountMicros = product.priceAmountMicros
        priceCurrencyCode = product.priceCurrencyCode
        subscriptionPeriod = product.subscriptionPeriod
        title = product.title
        description = product.description
    }
}
