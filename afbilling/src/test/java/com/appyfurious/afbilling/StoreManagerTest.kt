package com.appyfurious.afbilling

import org.junit.Assert.*
import org.junit.Test

class StoreManagerTest {

    private fun isNewProducts(inAppProductsId: List<String>, newInAppProductsId: List<String>): Boolean =
            !(inAppProductsId.toTypedArray() contentEquals newInAppProductsId.toTypedArray())

    @Test
    fun filter_set_product_true() {
        val value = arrayOf("product_1", "product_2", "product_2", "product_1", "product.3")
        val actual = LinkedHashSet<String>(value.toList()).toArray()
        assertArrayEquals(arrayOf("product_1", "product_2", "product.3"), actual)
    }

    @Test
    fun check_new_products_1() {
        val inAppProductsId = listOf("product_1", "product_2")
        val newInAppProductsId = listOf("product_1", "product_3")
        assertTrue(isNewProducts(inAppProductsId, newInAppProductsId))
    }

    @Test
    fun check_new_products_2() {
        val inAppProductsId = listOf("product_1", "product_2")
        val newInAppProductsId = listOf("product_2", "product_1", "product_3")
        assertTrue(isNewProducts(inAppProductsId, newInAppProductsId))
    }

    @Test
    fun check_new_products_3() {
        val inAppProductsId = listOf("product_1", "product_2")
        val newInAppProductsId = listOf<String>()
        assertTrue(isNewProducts(inAppProductsId, newInAppProductsId))
    }

    @Test
    fun check_old_products_1() {
        val inAppProductsId = listOf("product_1", "product_2")
        val newInAppProductsId = listOf("product_1", "product_2")
        assertFalse(isNewProducts(inAppProductsId, newInAppProductsId))
    }
}