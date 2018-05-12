package io.karn.notify

import android.support.v4.app.NotificationCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyAlertingTest : NotifyTestBase() {

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
    }

    @Test
    fun modifiedAlertingTest() {
        val testVisibility = NotificationCompat.VISIBILITY_PUBLIC

        val notification = Notify.with(this.context)
                .alerting {
                    lockScreenVisibility = testVisibility
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testVisibility, notification.visibility)
    }
}
