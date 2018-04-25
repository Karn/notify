package io.karn.notify

import android.content.Intent

class Creator(private val notify: Notify) {

    internal var meta = Notify.Meta()
    internal var header = Notify.Header()
    internal var content: Notify.Content = Notify.Content.Default()
    internal var stackable: Notify.Stackable? = null

    private var clickHandler: Intent? = null

    fun meta(meta: Notify.Meta.() -> Unit): Creator {
        meta(this.meta)

        return this
    }

    fun header(header: Notify.Header.() -> Unit): Creator {
        header(this.header)

        return this
    }

    fun content(block: Notify.Content.Default.() -> Unit): Creator {
        this.content = Notify.Content.Default()
        block(this.content as Notify.Content.Default)
        return this
    }

    fun stackable(block: Notify.Stackable.() -> Unit): Creator {
        this.stackable = Notify.Stackable()
        block(this.stackable as Notify.Stackable)
        return this
    }

    fun asBigText(block: Notify.Content.BigText.() -> Unit): Creator {
        this.content = Notify.Content.BigText()
        block(this.content as Notify.Content.BigText)
        return this
    }

    fun asBigPicture(block: Notify.Content.BigPicture.() -> Unit): Creator {
        this.content = Notify.Content.BigPicture()
        block(this.content as Notify.Content.BigPicture)
        return this
    }

    fun asProgress(block: Notify.Content.Progress.() -> Unit): Creator {
        this.content = Notify.Content.BigPicture()
        block(this.content as Notify.Content.Progress)
        return this
    }

    fun asMedia(block: Notify.Content.Media.() -> Unit): Creator {
        this.content = Notify.Content.Media()
        block(this.content as Notify.Content.Media)
        return this
    }

    fun asMessage(block: Notify.Content.Message.() -> Unit): Creator {
        this.content = Notify.Content.Message()
        block(this.content as Notify.Content.Message)
        return this
    }

    fun clickHandler(clickHander: Intent?): Creator {
        this.clickHandler = clickHander
        return this
    }

    fun send(): Int {
        notify.send(NotificationPayload(meta, header, content, stackable))
        return -1
    }
}
