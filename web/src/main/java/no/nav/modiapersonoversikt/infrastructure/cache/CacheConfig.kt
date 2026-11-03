package no.nav.modiapersonoversikt.infrastructure.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.CaffeineSpec
import io.lettuce.core.ClientOptions
import io.lettuce.core.SocketOptions
import io.micrometer.core.instrument.MeterRegistry
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.Organisasjon
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonSak
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.soknadsstatus.Soknadsstatus
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleCacheErrorHandler
import org.springframework.cache.interceptor.SimpleKeyGenerator
import org.springframework.cache.support.NullValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.env.Environment
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.databind.ser.std.ToStringSerializer
import tools.jackson.databind.util.StdConverter
import tools.jackson.module.kotlin.kotlinModule
import java.time.Duration
import java.util.regex.Pattern

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
open class CacheConfig(
    private val environment: Environment,
    private val meterRegistries: ObjectProvider<MeterRegistry>,
) : CachingConfigurer {
    private val brukValkey: Boolean
        get() =
            when (val backend = environment.getProperty("cache.backend", "valkey")) {
                "valkey" -> true
                "caffeine" -> false
                else -> throw IllegalStateException("Unsupported cache.backend: $backend")
            }

    @Bean
    open fun cacheManager(
        tilkoblinger: ObjectProvider<RedisConnectionFactory>,
        serialiserere: ObjectProvider<RedisSerializer<Any>>,
    ): CacheManager =
        if (brukValkey) {
            val app = environment.getProperty("NAIS_APP_NAME", "modiapersonoversikt-api")
            val cluster = environment.getProperty("NAIS_CLUSTER_NAME", "local")
            val namespace = environment.getProperty("NAIS_NAMESPACE", "personoversikt")
            val deployment = environment.getProperty("APP_ENVIRONMENT_NAME", "local")
            val prefiks = "cache:v2:$app:$cluster:$namespace:$deployment:"
            val standard =
                RedisCacheConfiguration
                    .defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(60))
                    .prefixCacheNameWith(prefiks)
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serialiserere.getObject()))
            RedisCacheManager
                .builder(tilkoblinger.getObject())
                .cacheDefaults(standard)
                .withInitialCacheConfigurations(
                    mapOf(
                        "endpointCache" to 3L,
                        "oppfolgingsinfoCache" to 300L,
                        "oppfolgingskontraktCache" to 600L,
                        "sykePengerCache" to 300L,
                        "utbetalingCache" to 1800L,
                        "pdlCache" to 3600L,
                        "representasjonApiCache" to 3600L,
                        "varslingCache" to 180L,
                        "aapCache" to 600L,
                    ).mapValues { (_, sekunder) -> standard.entryTtl(Duration.ofSeconds(sekunder)) },
                ).enableCreateOnMissingCache()
                .build()
        } else {
            CaffeineCacheManager().apply {
                this.setCaffeineSpec(
                    CaffeineSpec.parse(
                        """
                        expireAfterWrite=60m,
                        maximumSize=10000
                        """.trimIndent(),
                    ),
                )

                cache("endpointCache", 3, 10000)
                cache("oppfolgingsinfoCache", 300)
                cache("oppfolgingskontraktCache", 600)
                cache("sykePengerCache", 300)
                cache("utbetalingCache", 1800, 10000)
                cache("pdlCache", 3600, 100000)
                cache("representasjonApiCache", 3600, 10000)
                cache("varslingCache", 180, 10000)
                cache("aapCache", 600)
            }
        }

    @Bean
    @ConditionalOnProperty(name = ["cache.backend"], havingValue = "valkey", matchIfMissing = true)
    open fun cacheValueSerializer(): RedisSerializer<Any> {
        val typer =
            BasicPolymorphicTypeValidator
                .builder()
                .allowIfSubType("no.nav.modiapersonoversikt.api.domain.aap.generated.models.")
                .allowIfSubType("no.nav.modiapersonoversikt.arena.")
                .allowIfSubType("no.nav.modiapersonoversikt.infotrgd.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.pdl.generated.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.representasjon.generated.models.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.models.")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging$")
                .allowIfSubType("no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon$")
                .allowIfSubType("no.nav.modiapersonoversikt.service.varsel.VarslerService$")
                .allowIfSubType("no.nav.modiapersonoversikt.service.utbetaling.UtbetalingDomain$")
                .allowIfSubType("no.nav.modiapersonoversikt.commondomain.")
                .allowIfSubType(Organisasjon::class.java)
                .allowIfSubType(PensjonSak::class.java)
                .allowIfSubType(JournalforingSak::class.java)
                .allowIfSubType(PdlOppslagService.PdlSokResultat::class.java)
                .allowIfSubType(Soknadsstatus::class.java)
                .allowIfSubType(NullValue::class.java)
                .allowIfSubType(
                    Pattern.compile(
                        """java\.util\.(ArrayList|LinkedList|HashMap|LinkedHashMap|HashSet|LinkedHashSet|Arrays[$]ArrayList|Collections[$](Empty|Singleton)(List|Map|Set)|ImmutableCollections[$](List|Map|Set)(12|N))""",
                    ),
                ).allowIfSubType(Pattern.compile("""kotlin\.collections\.Empty(List|Map|Set)"""))
                .allowIfSubType(Pattern.compile("""java\.time\.(LocalDate|LocalDateTime|OffsetDateTime|ZonedDateTime|Instant)"""))
                .allowIfSubType(Pattern.compile("""java\.lang\.(String|Boolean|Integer|Long|Double|Float|Short|Byte)"""))
                .allowIfSubType(Pattern.compile("""java\.math\.(BigDecimal|BigInteger)"""))
                .build()
        return GenericJacksonJsonRedisSerializer
            .builder()
            .enableDefaultTyping(typer)
            .enableSpringCacheNullValueSupport()
            // Object sikrer typevalidering ved lesing og typeinformasjon for final-klasser ved skriving.
            .reader { leser, bytes, _ -> leser.readValue(bytes, Any::class.java) }
            .writer { skriver, verdi -> skriver.writerFor(Any::class.java).writeValueAsBytes(verdi) }
            .customize { mapper ->
                mapper
                    .addModule(kotlinModule())
                    .accessorNaming(DefaultAccessorNamingStrategy.Provider().withFirstCharAcceptance(true, true))
                    .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                    .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                    .addMixIn(Soknadsstatus::class.java, SoknadsstatusCacheMixin::class.java)
                    .addMixIn(JournalforingSak::class.java, JournalforingSakCacheMixin::class.java)
            }.build()
    }

    private abstract class SoknadsstatusCacheMixin {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        @JsonSerialize(using = ToStringSerializer::class)
        @JsonDeserialize(converter = CacheLocalDateTimeConverter::class)
        abstract fun getSistOppdatert(): kotlinx.datetime.LocalDateTime?
    }

    private abstract class JournalforingSakCacheMixin {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        @JsonSerialize(using = ToStringSerializer::class)
        var opprettetDato: java.time.OffsetDateTime? = null
    }

    internal class CacheLocalDateTimeConverter : StdConverter<String, kotlinx.datetime.LocalDateTime>() {
        override fun convert(value: String): kotlinx.datetime.LocalDateTime = kotlinx.datetime.LocalDateTime.parse(value)
    }

    @Bean("userkeygenerator")
    open fun userKeyGenerator(): KeyGenerator =
        if (brukValkey) JsonCacheKeyGenerator(inkluderBruker = true) else AutentisertBrukerKeyGenerator()

    @Bean("methodawarekeygenerator")
    open fun methodAwareKeyGenerator(): KeyGenerator =
        if (brukValkey) JsonCacheKeyGenerator(inkluderBruker = false) else MethodAwareKeyGenerator()

    @Bean("keyGenerator")
    open fun defaultKeyGenerator(): KeyGenerator = if (brukValkey) JsonCacheKeyGenerator(inkluderBruker = true) else SimpleKeyGenerator()

    override fun keyGenerator(): KeyGenerator = defaultKeyGenerator()

    override fun errorHandler(): CacheErrorHandler =
        if (brukValkey) ValkeyCacheErrorHandler(meterRegistries.getObject()) else SimpleCacheErrorHandler()

    @Bean
    @ConditionalOnProperty(name = ["cache.backend"], havingValue = "valkey", matchIfMissing = true)
    open fun redisConnectionFactory(): RedisConnectionFactory {
        val suffiks = "modiapersonoversikt-api"

        fun paakrevd(navn: String): String =
            (
                environment.getProperty("cache.valkey.$navn")
                    ?: environment.getProperty("VALKEY_${navn.uppercase()}_$suffiks")
            )?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException(
                    "Missing Valkey $navn configuration (cache.valkey.$navn or VALKEY_${navn.uppercase()}_$suffiks)",
                )

        val host = paakrevd("host")
        val port =
            paakrevd("port").toIntOrNull()?.takeIf { it in 1..65535 }
                ?: throw IllegalStateException("Invalid Valkey port configuration")
        val brukernavn = paakrevd("username")
        val passord = paakrevd("password")
        // TLS kan bare slås av med eksplisitte tilkoblingsinnstillinger for test.
        val ssl =
            if (environment.getProperty("cache.valkey.host") != null) {
                environment.getProperty("cache.valkey.ssl", Boolean::class.java, true)
            } else {
                true
            }
        val server =
            RedisStandaloneConfiguration(host, port).apply {
                setUsername(brukernavn)
                setPassword(
                    org.springframework.data.redis.connection.RedisPassword
                        .of(passord),
                )
            }
        val klientkonfig =
            LettuceClientConfiguration
                .builder()
                .commandTimeout(Duration.ofSeconds(2))
                .clientOptions(
                    ClientOptions
                        .builder()
                        .socketOptions(SocketOptions.builder().connectTimeout(Duration.ofSeconds(2)).build())
                        .build(),
                )
        if (ssl) klientkonfig.useSsl()
        return LettuceConnectionFactory(server, klientkonfig.build())
    }

    private fun CaffeineCacheManager.cache(
        name: String,
        time: Long,
        maximumSize: Long = 1000,
    ) {
        this.registerCustomCache(name, createCache(time, maximumSize).build())
    }

    companion object {
        fun createCache(
            time: Long,
            maximumSize: Long = 1000,
        ) = Caffeine
            .newBuilder()
            .recordStats()
            .expireAfterAccess(Duration.ofSeconds(time))
            .expireAfterWrite(Duration.ofSeconds(time))
            .maximumSize(maximumSize)
    }
}
