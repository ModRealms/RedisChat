package net.modrealms.redischat.server.datastore.redis

import com.google.gson.Gson
import com.mojang.logging.LogUtils
import net.modrealms.redischat.server.config.ModConfiguration
import net.modrealms.redischat.server.data.ChatMessage
import net.modrealms.redischat.server.datastore.AbstractDatastore
import net.modrealms.redischat.server.datastore.redis.listener.ServerChatPubSub
import net.modrealms.redischat.server.datastore.redis.util.RedisConstants
import org.redisson.Redisson
import org.redisson.api.RPatternTopic
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.slf4j.Logger


class RedisDatastore : AbstractDatastore() {
    private val logger: Logger = LogUtils.getLogger()
    private var serverKey: String = ModConfiguration.get("redis.serverKey")
    private var channelName: String = ModConfiguration.get("redis.channelName")
    private var channelDelimiter = RedisConstants.CHANNEL_DELIMITER

    private lateinit var redisson: RedissonClient

    override fun init() {
        val host: String = ModConfiguration.get("redis.host")
        val port: Int = ModConfiguration.get("redis.port")
        val username: String = ModConfiguration.get("redis.username")
        val password: String = ModConfiguration.get("redis.password")
        val ssl: Boolean = ModConfiguration.get("redis.ssl")

        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$host:$port")
            .setUsername(username)
            .setPassword(password)
            .setSslEnableEndpointIdentification(ssl)

        try {
            this.redisson = Redisson.create(config)
        } catch (e: Exception) {
            throw IllegalArgumentException("There was an error while trying to connect to the Redis server. ${e.message}")
        }
    }

    override fun postServerStart() {
        this.logger.info("Setting up pub-sub for server chat...")
        val topic: RPatternTopic = this.redisson.getPatternTopic("$channelName$channelDelimiter*", StringCodec.INSTANCE)
        topic.addListenerAsync(String::class.java, ServerChatPubSub())
    }

    override fun shutdown() {
        this.redisson.shutdown()
    }

    override fun publishMessage(message: ChatMessage) {
        this.redisson.getTopic("$channelName$channelDelimiter$serverKey", StringCodec.INSTANCE)
            .publishAsync(Gson().toJson(message, ChatMessage::class.java))
    }
}
