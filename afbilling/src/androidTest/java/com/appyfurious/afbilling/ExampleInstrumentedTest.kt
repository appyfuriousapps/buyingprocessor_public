package com.appyfurious.afbilling

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.appyfurious.afbilling.utils.Adverting
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun adverting_value_not_null_0() {
        val context = InstrumentationRegistry.getTargetContext()
        Adverting(context) {
            assertNotNull(it)
        }
    }

    @Test
    fun adverting_value_not_null_1() {
        val context = InstrumentationRegistry.getTargetContext()
        Adverting(context) {
            assertNotNull(it)
        }
    }

    @Test
    fun adverting_value_not_null_2() {
        val context = InstrumentationRegistry.getTargetContext()
        Adverting(context) {
            assertNotNull(it)
        }
    }
}
