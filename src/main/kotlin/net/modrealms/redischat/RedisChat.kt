package net.modrealms.redischat

import com.mojang.logging.LogUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.modrealms.redischat.server.config.ModConfiguration
import net.modrealms.redischat.server.datastore.AbstractDatastore
import net.modrealms.redischat.server.datastore.redis.RedisDatastore
import net.modrealms.redischat.server.event.ServerChatHandler
import org.slf4j.Logger

@Mod("redischat")
object RedisChat {
    private val logger: Logger = LogUtils.getLogger()
    lateinit var datastore: AbstractDatastore

    init {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(ServerChatHandler())
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModConfiguration.configurationSpec)
    }

    @SubscribeEvent
    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
        this.datastore = RedisDatastore()
    }

    @SubscribeEvent
    fun onServerStarted(event: ServerStartedEvent) {
        this.datastore.postServerStart()
    }

    @SubscribeEvent
    fun onServerStopping(event: ServerStoppingEvent) {
        this.datastore.shutdown()
    }
}
