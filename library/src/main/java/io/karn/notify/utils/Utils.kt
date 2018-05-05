package io.karn.notify.utils

import android.text.Html
import java.util.*

internal object Utils {
    fun getRandomInt(): Int {
        return Random().nextInt(Int.MAX_VALUE - 100) + 100
    }

    fun getAsSecondaryFormattedText(str: String?): CharSequence? {
        str ?: return null

        return Html.fromHtml("<font color='#3D3D3D'>$str</font>")
    }
}
