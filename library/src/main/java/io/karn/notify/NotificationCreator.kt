package io.karn.notify

import android.content.Intent
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.Payload
import io.karn.notify.entities.RawNotification

class Creator(private val notify: Notify, config: NotifyConfig = NotifyConfig()) {

    private var meta = Payload.Meta()
    private var header = config.header
    private var content: Payload.Content = Payload.Content.Default()
    private var stackable: Payload.Stackable? = null

    private var clickHandler: Intent? = null

    fun meta(meta: Payload.Meta.() -> Unit): Creator {
        meta(this.meta)

        return this
    }

    fun header(header: Payload.Header.() -> Unit): Creator {
        header(this.header)

        return this
    }

    fun content(block: Payload.Content.Default.() -> Unit): Creator {
        this.content = Payload.Content.Default()
        block(this.content as Payload.Content.Default)
        return this
    }

    fun asTextList(block: Payload.Content.TextList.() -> Unit): Creator {
        this.content = Payload.Content.TextList()
        block(this.content as Payload.Content.TextList)
        return this
    }

    fun asBigText(block: Payload.Content.BigText.() -> Unit): Creator {
        this.content = Payload.Content.BigText()
        block(this.content as Payload.Content.BigText)
        return this
    }

    fun asBigPicture(block: Payload.Content.BigPicture.() -> Unit): Creator {
        this.content = Payload.Content.BigPicture()
        block(this.content as Payload.Content.BigPicture)
        return this
    }

    fun asMessage(block: Payload.Content.Message.() -> Unit): Creator {
        this.content = Payload.Content.Message()
        block(this.content as Payload.Content.Message)
        return this
    }

    fun stackable(block: Payload.Stackable.() -> Unit): Creator {
        this.stackable = Payload.Stackable()
        block(this.stackable as Payload.Stackable)
        return this
    }

    fun clickHandler(clickHandler: Intent?): Creator {
        this.clickHandler = clickHandler
        return this
    }

    fun send(): Int {
        notify.send(RawNotification(meta, header, content, stackable))
        return -1
    }
}
