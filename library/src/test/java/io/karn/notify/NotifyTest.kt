package io.karn.notify

import android.app.Application
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
open class NotifyTest {
    protected val context: Application = RuntimeEnvironment.application
}
