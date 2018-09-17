package com.appyfurious.afbilling

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.appyfurious.afbilling.utils.Adverting
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdvertingTest {

    @Test
    fun read_adverting_1() {
        val context = InstrumentationRegistry.getTargetContext()
        Adverting(context) {
            assertNotNull(it)
        }
    }
}