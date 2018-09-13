package com.appyfurious.validation

object ValidKeys {

    var baseUrl = ""
    var apiKey = ""
    var secretKey = ""

    fun init(baseUrl: String, apiKey: String, secretKey: String) {
        this.baseUrl = baseUrl
        this.apiKey = apiKey
        this.secretKey = secretKey
    }
}