package io.karn.notify

import android.support.v4.app.NotificationCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotifyAlertingTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun defaultAlertingTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(0, notification.visibility)
        Assert.assertEquals(0, notification.timeoutAfter)
    }

    @Test
    fun modifiedAlertingTest() {
        val testVisibility = NotificationCompat.VISIBILITY_PUBLIC
        val testTimeout = 5000L

        val notification = Notify.with(this.context)
                .alerting {
                    lockScreenVisibility = testVisibility
                    timeout = testTimeout
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testVisibility, notification.visibility)
        Assert.assertEquals(testTimeout, notification.timeoutAfter)
    }
}
