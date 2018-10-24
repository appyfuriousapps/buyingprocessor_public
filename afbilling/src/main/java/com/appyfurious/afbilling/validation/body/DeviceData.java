package com.appyfurious.afbilling.validation.body;

import com.google.gson.annotations.SerializedName;

/**
 * DeviceData.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class DeviceData {

    @SerializedName("appsflyerId")
    private String appsflyerId;
    @SerializedName("idfa")
    private String idfa;

    public DeviceData(String appsflyerId, String idfa) {
        this.appsflyerId = appsflyerId;
        this.idfa = idfa;
    }

    public String getAppsflyerId() {
        return appsflyerId;
    }

    public void setAppsflyerId(String appsflyerId) {
        this.appsflyerId = appsflyerId;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    @Override
    public String toString() {
        return "appsflyerId:" + appsflyerId + " idfa:" + idfa;
    }
}