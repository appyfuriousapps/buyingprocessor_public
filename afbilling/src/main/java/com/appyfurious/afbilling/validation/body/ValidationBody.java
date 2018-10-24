package com.appyfurious.afbilling.validation.body;

import com.google.gson.annotations.SerializedName;

/**
 * ValidationBody.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class ValidationBody {

    public static final String PRODUCT_TYPE = "subscription";

    @SerializedName("hash") private String hash;
    @SerializedName("purchaseToken") private String purchaseToken;
    @SerializedName("productId") private String productId;
    @SerializedName("type") private String type;
    @SerializedName("package") private String _package;
    @SerializedName("developerPayload") private String developerPayload;
    @SerializedName("deviceData") private DeviceData deviceData;

    public ValidationBody(String hash, String purchaseToken, String productId, String type,
                          String _package, String developerPayload, String appsflyerId, String idfa) {
        this.hash = hash;
        this.purchaseToken = purchaseToken;
        this.productId = productId;
        this.type = type;
        this._package = _package;
        this.developerPayload = developerPayload;
        this.deviceData = new DeviceData(appsflyerId, idfa);
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    @Override
    public String toString() {
        return "hash:" + hash + " purchaseToken:" + purchaseToken + " productId:" + productId + " type:" + type + " _package:" +
                _package + " developerPayload:" + developerPayload + " deviceData.getAppsflyerId:" + deviceData.getAppsflyerId()
                + "deviceData.getIdfa():" + deviceData.getIdfa();
    }
}