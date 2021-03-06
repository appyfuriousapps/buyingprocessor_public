package com.appyfurious.afbilling.product

import com.appyfurious.afbilling.AFStoreManager
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

    fun isActive() = purchaseState == AFStoreManager.Keys.PURCHASE_STATUS_PURCHASED
}
