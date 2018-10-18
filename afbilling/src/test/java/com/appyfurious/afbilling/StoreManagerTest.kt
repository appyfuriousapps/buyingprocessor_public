package com.appyfurious.afbilling

import org.junit.Assert.*
import org.junit.Test

class StoreManagerTest {

    @Test
    fun filter_set_product_true() {
        val value = arrayOf("product_1", "product_2", "product_2", "product_1", "product.3")
        val actual = LinkedHashSet<String>(value.toList()).toArray()
        assertArrayEquals(arrayOf("product_1", "product_2", "product.3"), actual)
    }
}