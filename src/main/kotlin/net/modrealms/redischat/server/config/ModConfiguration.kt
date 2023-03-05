package net.modrealms.redischat.server.config

import com.mojang.logging.LogUtils
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import org.slf4j.Logger

object ModConfiguration {
    private val logger: Logger = LogUtils.getLogger()

    val configurationSpec: ForgeConfigSpec = ForgeConfigSpec.Builder()
        .comment("Connection host to connect to.")
        .define("redis.host", "localhost").next()
        .comment("Connection port to connect to.")
        .define("redis.port", 6379).next()
        .comment("Whether or not to create the Redis connection over SSL/TLS.")
        .define("redis.ssl", false).next()
        .comment("Username to authenticate with.")
        .define("redis.username", "default").next()
        .comment("Redis password to authenticate with.")
        .define("redis.password", "").next()
        .comment("The parent channel name to use for pub-sub requests")
        .define("redis.channelName", "server").next()
        .comment("The simple server name to use within the parent channel pub-sub.")
        .define("redis.serverKey", "server1").next()
        .comment("The server name to identify this server by within displayed messages.")
        .define("messages.serverName", "My Server").next()
        .comment("Whether or not to show the server's prefix on messages from the current server.")
        .define("messages.showPrefixOnSameServer", false).next()
        .build()

    fun <T> get(path: String): T {
        val configValue = this.configurationSpec.values?.get<ConfigValue<T>>(path)
        if (configValue != null) {
            return this.configurationSpec.values.get<ConfigValue<T>>(path).get()
        }
        throw NoSuchFieldException("Configuration value for path $path not found -- was the wrong path specified in the code?")
    }
}