package com.appyfurious.afbilling

import com.appyfurious.afbilling.product.InAppProduct
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ParsePriceTest {

    @Test
    fun parse_price_is_correct_0() {
        val product = InAppProduct()
        product.price = "$0"
        val expected = 0.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_1() {
        val product = InAppProduct()
        product.price = "$22.99"
        val expected = 22.99
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_2() {
        val product = InAppProduct()
        product.price = "$5.99"
        val expected = 5.99
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_3() {
        val product = InAppProduct()
        product.price = "$2.5"
        val expected = 2.5
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_4() {
        val product = InAppProduct()
        product.price = "$5"
        val expected = 5.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_5() {
        val product = InAppProduct()
        product.price = "$0.9"
        val expected = 0.9
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_6() {
        val product = InAppProduct()
        product.price = null
        val expected = 0.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_7() {
        val product = InAppProduct()
        product.price = "$-0.5"
        val expected = 0.5
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_8() {
        val product = InAppProduct()
        product.price = "$0.0"
        val expected = 0.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_9() {
        val product = InAppProduct()
        product.price = "1.1$"
        val expected = 1.1
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_10() {
        val product = InAppProduct()
        product.price = "49.99 $"
        val expected = 49.99
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_11() {
        val product = InAppProduct()
        product.price = "5$"
        val expected = 5.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_12() {
        val product = InAppProduct()
        product.price = "-5$"
        val expected = 5.0
        assertEquals(expected, 0.0, product.getPriceParse())
    }

    @Test
    fun parse_price_is_correct_13() {
        val product = InAppProduct()
        product.price = "19,99 $"
        val expected = 19.99
        assertEquals(expected, 0.0, product.getPriceParse())
    }
}