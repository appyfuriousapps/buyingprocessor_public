package com.appyfurious.afbilling.utils

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeveloperPayload {

    @SerializedName("appsflyerId")
    @Expose
    var appsflyerId: String? = null

    @SerializedName("idfa")
    @Expose
    var advertingId: String? = null
}