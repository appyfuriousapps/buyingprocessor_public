package com.appyfurious.afbilling.product

import android.util.Base64
import com.appyfurious.afbilling.utils.DeveloperPayload
import com.appyfurious.log.Logger
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.nio.charset.Charset
import java.util.regex.Pattern

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

    @SerializedName("devPayload")
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
    val priceAmountMicros: Int? = null

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

    fun getDeveloperPayload(): ByteArray {
        val result = Base64.decode(developerPayload, Base64.DEFAULT)
        Logger.notify("getDevPayload BYTE: $result")
        return result
    }

    fun setDeveloperPayload(devPayload: DeveloperPayload) {
        val gson = GsonBuilder().create()
        val obj = gson.toJson(devPayload)
        developerPayload = String(Base64.encode(obj.toByteArray(), Base64.DEFAULT), Charset.defaultCharset())
    }

    fun getAppsflyerId(): String {
        val gson = GsonBuilder().create()
        val value = Base64.decode(developerPayload, Base64.DEFAULT).toString()
        val result = gson.fromJson(value, DeveloperPayload::class.java)
        Logger.notify("getAppsflyerId $result")
        return result.appsflyerId ?: ""
    }

    fun getAdvertingId(): String {
        val gson = GsonBuilder().create()
        val value = Base64.decode(developerPayload, Base64.DEFAULT).toString()
        val result = gson.fromJson(value, DeveloperPayload::class.java)
        Logger.notify("getAdvertingId $result")
        return result.advertingId ?: ""
    }

    fun getSku(): String? = productId

    fun getType() = SUBS

    fun gerPriceParse(): Double {
        var result = 0.0
        val resultParse = Pattern.compile("([0-9]+.*[0-9]*)").matcher(price)
        if (resultParse.find()) {
            val resultText = resultParse.group()
            result = resultText.toDouble()
        }
        return result
    }
}
