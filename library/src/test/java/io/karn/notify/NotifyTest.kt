package io.karn.notify

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyTest {

    @Test
    fun initializationTest() {
        Notify.defaultConfig {
            it.header.icon = R.drawable.ic_android_black
            it.header.color = android.R.color.darker_gray
        }
    }
}
