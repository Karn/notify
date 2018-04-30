package io.karn.notify

import android.text.Html
import java.util.concurrent.ThreadLocalRandom

internal object Utils {
    fun getRandomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, Int.MAX_VALUE)
    }

    fun getAsSecondaryFormattedText(str: String): CharSequence {
        return Html.fromHtml("<font color='#3D3D3D'>$str</font>")
    }
}
