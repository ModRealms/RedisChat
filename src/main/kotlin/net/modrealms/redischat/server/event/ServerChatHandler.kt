package net.modrealms.redischat.server.event

import com.mojang.logging.LogUtils
import net.minecraft.Util
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.MinecraftServer
import net.minecraft.server.TickTask
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.server.ServerLifecycleHooks
import net.modrealms.redischat.RedisChat
import net.modrealms.redischat.server.config.ModConfiguration
import net.modrealms.redischat.server.data.ChatMessage
import net.modrealms.redischat.server.datastore.redis.listener.RedisMessageEvent
import org.slf4j.Logger
import java.time.Instant

class ServerChatHandler {

    private val logger: Logger = LogUtils.getLogger()

    @SubscribeEvent
    fun onServerChat(event: ServerChatEvent) {
        event.isCanceled = true
        RedisChat.datastore.publishMessage(
            ChatMessage(
                ModConfiguration.get<String>("messages.serverName"),
                event.username,
                event.filteredMessage,
                Instant.now().epochSecond
            )
        )
    }

    @SubscribeEvent
    fun onRedisMessage(event: RedisMessageEvent) {
        val server: MinecraftServer = ServerLifecycleHooks.getCurrentServer()
        val showPrefixOnSameServer = ModConfiguration.get<Boolean>("messages.showPrefixOnSameServer")
        val message = event.message
        val component = TextComponent.EMPTY.plainCopy().run {
            if (!event.fromCurrentServer || showPrefixOnSameServer) {
                this.append(
                    TextComponent("[${message.serverName}]").withStyle(
                        Style.EMPTY.withColor(
                            TextColor.parseColor(
                                "#7E7E7E"
                            )
                        )
                    )
                )
            }
            this.append(TextComponent("${message.username}:").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#BB8DD1"))))
            this.append(TextComponent(" ${message.content}").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#DCDCDC"))))
        }


        server.doRunTask(TickTask(1) {
            server.playerList.players.forEach {
                it.sendMessage(component, Util.NIL_UUID)
            }
        })
    }
}