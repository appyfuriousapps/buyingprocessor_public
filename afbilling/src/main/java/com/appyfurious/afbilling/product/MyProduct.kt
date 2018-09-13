package com.appyfurious.afbilling.product

import android.util.Base64
import com.appyfurious.log.Logger
import com.appyfurious.validation.body.DeviceData
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyProduct {

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
    var purchaseTime: Int? = null

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
}
