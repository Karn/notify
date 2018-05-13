package io.karn.notify.internal.utils

import android.support.annotation.IntDef
import io.karn.notify.Notify

@DslMarker
annotation class NotifyScopeMarker

@Retention(AnnotationRetention.SOURCE)
@IntDef(Notify.IMPORTANCE_MIN,
        Notify.IMPORTANCE_LOW,
        Notify.IMPORTANCE_NORMAL,
        Notify.IMPORTANCE_HIGH,
        Notify.IMPORTANCE_MAX)
annotation class NotifyImportance
