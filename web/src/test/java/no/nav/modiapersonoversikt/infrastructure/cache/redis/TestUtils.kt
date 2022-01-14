package no.nav.modiapersonoversikt.infrastructure.cache.redis

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import java.time.Duration
import java.util.*

object TestUtils {

    class RedisContainer : GenericContainer<RedisContainer>("redis:6-alpine") {
        init {
            withExposedPorts(6379)
        }
    }

    class RedisSubscriber(private val subscribeCallback: () -> Unit = {}) : JedisPubSub() {
        private val messages: MutableList<Pair<String, String>> = mutableListOf()
        private val lock = WaitLock(false)

        override fun onPMessage(pattern: String, channel: String, message: String) {
            val messagePair = Pair(channel, message)
            messages.add(messagePair)
            if (messagePair == lock.keyOwner) {
                lock.unlock(messagePair)
            }
        }

        override fun onPSubscribe(pattern: String?, subscribedChannels: Int) {
            subscribeCallback()
        }

        suspend fun assertReceivedMessage(channel: String, message: String) {
            val messagePair = Pair(channel, message)
            if (!messages.contains(messagePair)) {
                lock.lock(messagePair)
                lock.waitForUnlock()
            }
        }
    }

    interface WithRedis {
        companion object {
            private var job: Job? = null
            private var subscriber: RedisSubscriber? = null
            private val container = RedisContainer()

            @BeforeAll
            @JvmStatic
            fun startContainer() {
                container.start()
            }

            @AfterAll
            @JvmStatic
            fun stopContainer() {
                container.stop()
            }
        }

        @BeforeEach
        fun setupSubscriber() = runBlocking {
            val lock = WaitLock(true)
            subscriber = RedisSubscriber { lock.unlock() }
            val jedis = Jedis(redisHostAndPort())
            job = GlobalScope.launch {
                jedis.psubscribe(subscriber, "*")
            }
            lock.waitForUnlock()
        }

        @AfterEach
        fun closeSubscriber() {
            subscriber?.unsubscribe()
            job?.cancel()
            subscriber = null
            job = null
        }

        fun redisHostAndPort() = HostAndPort(container.host, container.getMappedPort(6379))

        fun getRedisValue(key: String): String = Jedis(redisHostAndPort()).get(key)
        fun getRedisKeys(pattern: String): List<String> = Jedis(redisHostAndPort()).keys(pattern).toList()

        suspend fun assertReceivedMessage(channel: String, message: String, timeout: Duration = Duration.ofSeconds(1)) {
            withTimeout(timeout.toMillis()) {
                subscriber?.assertReceivedMessage(channel, message)
            }
        }
    }

    class WaitLock(initiallyLocked: Boolean = false) {
        private val mutex = Mutex(initiallyLocked)
        var keyOwner: Any? = null

        suspend fun lock(key: Any? = null) {
            mutex.lock(key)
            keyOwner = key
        }

        fun unlock(key: Any? = null) {
            mutex.unlock(key)
            keyOwner = null
        }

        suspend fun waitForUnlock() {
            val key = UUID.randomUUID()
            lock(key)
            unlock(key)
        }
    }
}
