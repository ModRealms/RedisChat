package net.modrealms.redischat.server.datastore.redis.listener

import com.google.gson.Gson
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.Event
import net.modrealms.redischat.server.config.ModConfiguration
import net.modrealms.redischat.server.data.ChatMessage
import net.modrealms.redischat.server.datastore.redis.util.RedisConstants
import org.redisson.api.listener.PatternMessageListener

class ServerChatPubSub : PatternMessageListener<String> {
    override fun onMessage(pattern: CharSequence?, channel: CharSequence?, msg: String) {
        Gson().run {
            // Using the redis configuration, determine whether the message is being sent from the current server's channel.
            val currentChannel =
                "${ModConfiguration.get<String>("redis.channelName")}${RedisConstants.CHANNEL_DELIMITER}${
                    ModConfiguration.get<String>("redis.serverKey")
                }"
            println(currentChannel)
            println(channel)
            MinecraftForge.EVENT_BUS.post(
                RedisMessageEvent(
                    channel,
                    pattern,
                    this.fromJson(msg, ChatMessage::class.java),
                    (currentChannel == channel.toString())
                )
            )
        }
    }
}

data class RedisMessageEvent(
    val channel: CharSequence?,
    val pattern: CharSequence?,
    val message: ChatMessage,
    val fromCurrentServer: Boolean
) : Event()