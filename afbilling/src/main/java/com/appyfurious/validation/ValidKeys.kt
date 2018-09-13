package com.appyfurious.validation

object ValidKeys {

    var baseUrl = ""
        private set
    var apiKey = ""
        private set
    var secretKey = ""
        private set

    fun init(baseUrl: String, apiKey: String, secretKey: String) {
        this.baseUrl = baseUrl
        this.apiKey = apiKey
        this.secretKey = secretKey
    }

    fun isNotNull() = baseUrl != "" && apiKey != "" && secretKey != ""
}