package net.modrealms.redischat.server.datastore

import net.modrealms.redischat.server.data.ChatMessage

abstract class AbstractDatastore {
    abstract fun publishMessage(message: ChatMessage)
    abstract fun init()
    abstract fun postServerStart()
    abstract fun shutdown()

    init {
        init()
    }
}