package com.appyfurious.afbilling

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
    val price: String? = null

    @SerializedName("price_amount_micros")
    @Expose
    val priceAmountMicros: Int? = null

    @SerializedName("price_currency_code")
    @Expose
    val priceCurrencyCode: String? = null

    @SerializedName("subscriptionPeriod")
    @Expose
    val subscriptionPeriod: String? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("description")
    @Expose
    val description: String? = null

    fun getSku(): String? = productId

    fun getType() = SUBS

    override fun toString() = "$orderId, $packageName, $productId, $purchaseTime," +
            "$purchaseState, $developerPayload, $purchaseToken, $autoRenewing, $price," +
            "$priceAmountMicros, $priceCurrencyCode, $subscriptionPeriod, $title, $description"
}
