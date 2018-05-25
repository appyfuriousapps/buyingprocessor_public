package com.appyfurious.afbilling

open class ProductPreview(
        val id: String,
        var title: String = "'",
        var description: String = "",
        var price: String = "",
        var appProduct: InAppProduct? = null,
        var visibleView: Boolean) {

    constructor(id: String, title: String, description: String, visibleView: Boolean = false):
            this(id, title, description, "", null, visibleView)
}