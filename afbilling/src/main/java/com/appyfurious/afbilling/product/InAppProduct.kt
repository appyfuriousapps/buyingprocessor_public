package com.appyfurious.afbilling.product

import android.util.Base64
import com.appyfurious.log.Logger
import com.appyfurious.validation.body.DeviceData
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class InAppProduct {

    companion object {
        const val SUBS = "subs"
    }

    @SerializedName("productId")
    @Expose
    var productId: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

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

    fun getSku(): String? = productId

    fun getPriceParse(): Double {
        var textResult = price?.replace("-", "")
                ?.replace("$", "")
                ?.replace(",", ".")
                ?.replace(" ", "")
                ?.trim() ?: ""
        if (textResult.find { it == '.' } == null)
            textResult += ".0"
        return try {
            var index = 0
            textResult.mapIndexed { i, c ->
                if (c == '.') {
                    index = textResult.length - 1 - i
                }
            }
            textResult = textResult.replace(".", "").replace(" ", "").trim()
            val result = textResult.toInt().div(Math.pow(10.0, index.toDouble()))
            result
        } catch (ex: Exception) {
            Logger.exception(ex.message ?: "exception!")
            Logger.exception(ex)
            1.0
        }
    }

    fun getNewDeveloperPayloadBase64(deviceData: DeviceData) = try {
        val gson = GsonBuilder().create()
        val obj = gson.toJson(deviceData)
        val result = String(Base64.encode(obj.toByteArray(), Base64.DEFAULT))
        Logger.notify("new DeveloperPayload $obj")
        Logger.notify("new DeveloperPayload $result")
        result.replace("\\n", "").replace("\n", "")
    } catch (ex: Exception) {
        ""
    }
}
