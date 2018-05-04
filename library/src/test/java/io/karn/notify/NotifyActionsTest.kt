package io.karn.notify

import android.app.Application
import io.karn.notify.utils.Action
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotifyActionsTest {

    private val context: Application = RuntimeEnvironment.application

    @Test
    fun defaultActionsTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertNull(notification.actions)
    }

    @Test
    fun modifiedActionsTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .actions {
                    add(Action(
                            R.drawable.ic_android_black,
                            "Action",
                            null
                    ))
                }
                .asBuilder()
                .build()

        Assert.assertNotNull(notification.actions)
        Assert.assertEquals(1, notification.actions.size)
    }
}
