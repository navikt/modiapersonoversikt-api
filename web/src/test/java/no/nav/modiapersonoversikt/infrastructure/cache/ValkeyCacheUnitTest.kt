package no.nav.modiapersonoversikt.infrastructure.cache

import com.github.benmanes.caffeine.cache.Caffeine
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.OppfolgingskontraktResponse
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Ytelse
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.YtelseskontraktResponse
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.GeografiskTilknytning
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonSak
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.PdlFelt
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.PdlKriterie
import no.nav.modiapersonoversikt.service.soknadsstatus.Soknadsstatus
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.NullValue
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.cache.RedisCache
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier

class ValkeyCacheUnitTest {
    @Test
    fun `valkey manager preserves named and default expiry`() {
        val kontekst = disconnectedContext()
        kontekst.use {
            val cacher = kontekst.getBean(RedisCacheManager::class.java)
            assertEquals(
                Duration.ofSeconds(3),
                (cacher.getCache("endpointCache") as RedisCache).cacheConfiguration.ttlFunction.getTimeToLive("key", "value"),
            )
            assertEquals(
                Duration.ofSeconds(300),
                (cacher.getCache("oppfolgingsinfoCache") as RedisCache).cacheConfiguration.ttlFunction.getTimeToLive("key", "value"),
            )
            assertEquals(
                Duration.ofHours(1),
                (cacher.getCache("pdlCache") as RedisCache).cacheConfiguration.ttlFunction.getTimeToLive("key", "value"),
            )
            assertEquals(
                Duration.ofHours(1),
                (cacher.getCache("soknadsstatusCache") as RedisCache).cacheConfiguration.ttlFunction.getTimeToLive("key", "value"),
            )
        }
    }

    @Test
    fun `key preserves JSON format for search criteria dates and sorted maps`() {
        val tjeneste = CacheProbe()
        val metode = tjeneste.javaClass.getMethod("person", String::class.java)
        val generator = JsonCacheKeyGenerator(inkluderBruker = false)
        val argumenter =
            listOf(
                PdlKriterie(PdlFelt.FORNAVN, "Test"),
                LocalDate.of(2026, 1, 2),
                linkedMapOf("b" to "two", "a" to "one"),
                NorskArgument("test"),
            )
        val nokkel = generator.generate(tjeneste, metode, argumenter)
        assertEquals(
            """["${CacheProbe::class.java.name}","person",["java.lang.String"],null,[[{"boost":null,"felt":"FORNAVN","searchHistorical":"HISTORISK_OG_GJELDENDE","value":"Test"},[2026,1,2],{"a":"one","b":"two"},{"årsak":"test"}]]]""",
            nokkel,
        )
        assertEquals(nokkel, JsonCacheKeyGenerator(inkluderBruker = false).generate(tjeneste, metode, argumenter))
    }

    @Test
    fun `unavailable Valkey bypasses cache without hiding source errors`() {
        disconnectedContext().use { kontekst ->
            val probe = kontekst.getBean(CacheProbe::class.java)
            AuthContextTestUtils.withIdent("Z123456", UnsafeSupplier { probe.person("12345678901") })
            AuthContextTestUtils.withIdent("Z123456", UnsafeSupplier { probe.person("12345678901") })
            assertEquals(2, (AopProxyUtils.getSingletonTarget(probe) as CacheProbe).kall.get())
            assertThrows(IllegalArgumentException::class.java) {
                AuthContextTestUtils.withIdent("Z123456", UnsafeSupplier { probe.broken("12345678901") })
            }
            val metrikker = kontekst.getBean(MeterRegistry::class.java)
            assertEquals(3.0, metrikker.counter("valkey_cache_bypass_total", "cache", "pdlCache", "operation", "get").count())
        }
    }

    @Test
    fun `key is plaintext and isolates method arguments and authenticated user`() {
        val tjeneste = CacheProbe()
        val metode = tjeneste.javaClass.getMethod("person", String::class.java)
        val annenMetode = tjeneste.javaClass.getMethod("other", String::class.java)
        val generator = JsonCacheKeyGenerator(inkluderBruker = true)

        val forste = AuthContextTestUtils.withIdent("Z123456", UnsafeSupplier { generator.generate(tjeneste, metode, "12345678901") })
        val igjen = AuthContextTestUtils.withIdent("Z123456", UnsafeSupplier { generator.generate(tjeneste, metode, "12345678901") })
        val annenBruker = AuthContextTestUtils.withIdent("Z654321", UnsafeSupplier { generator.generate(tjeneste, metode, "12345678901") })
        val annetArgument =
            AuthContextTestUtils.withIdent(
                "Z123456",
                UnsafeSupplier { generator.generate(tjeneste, metode, "10987654321") },
            )
        val annetKall =
            AuthContextTestUtils.withIdent(
                "Z123456",
                UnsafeSupplier { generator.generate(tjeneste, annenMetode, "12345678901") },
            )

        assertEquals(forste, igjen)
        assertNotEquals(forste, annenBruker)
        assertNotEquals(forste, annetArgument)
        assertNotEquals(forste, annetKall)
        assertEquals(
            """["${CacheProbe::class.java.name}","person",["java.lang.String"],"Z123456",["12345678901"]]""",
            forste,
        )
    }

    @Test
    fun `serializer round trips cache models and collections`() {
        disconnectedContext().use { kontekst ->
            val serialiserer = kontekst.getBean<RedisSerializer<Any>>()

            assertEquals("text", serialiserer.deserialize(serialiserer.serialize("text")))
            assertEquals(listOf("one", "two"), serialiserer.deserialize(serialiserer.serialize(listOf("one", "two"))))
            assertEquals(emptyList<String>(), serialiserer.deserialize(serialiserer.serialize(emptyList<String>())))
            val persondata = HentPersondata.Result(hentGeografiskTilknytning = GeografiskTilknytning(gtKommune = "0301"))
            assertEquals(persondata, serialiserer.deserialize(serialiserer.serialize(persondata)))
            assertEquals(listOf(persondata), serialiserer.deserialize(serialiserer.serialize(listOf(persondata))))
            assertEquals(mapOf("person" to persondata), serialiserer.deserialize(serialiserer.serialize(mapOf("person" to persondata))))
            assertEquals(java.util.List.of("one", "two"), serialiserer.deserialize(serialiserer.serialize(java.util.List.of("one", "two"))))
            assertInstanceOf(NullValue::class.java, serialiserer.deserialize(serialiserer.serialize(NullValue.INSTANCE)))
            assertEquals(null, serialiserer.deserialize(serialiserer.serialize(null)))
            val pensjon = PensjonSak(1L, "ALDER", "AKTIV", LocalDate.of(2026, 1, 1), null, "0301")
            assertEquals(pensjon, serialiserer.deserialize(serialiserer.serialize(pensjon)))
            val status = mapOf("AAP" to Soknadsstatus(sistOppdatert = kotlinx.datetime.LocalDateTime(2026, 1, 1, 12, 0)))
            assertEquals(status, serialiserer.deserialize(serialiserer.serialize(status)))
            val tidspunkt = ZonedDateTime.parse("2026-01-01T12:00:00+01:00[Europe/Oslo]")
            assertEquals(tidspunkt, serialiserer.deserialize(serialiserer.serialize(tidspunkt)))
            val sak =
                JournalforingSak().apply {
                    opprettetDato =
                        java.time.OffsetDateTime
                            .parse("2026-01-01T12:00:00+01:00")
                }
            val gjenopprettet = assertInstanceOf(JournalforingSak::class.java, serialiserer.deserialize(serialiserer.serialize(sak)))
            assertEquals(sak.opprettetDato, gjenopprettet.opprettetDato)
        }
    }

    @Test
    fun `serializer round trips ytelseskontrakt with LocalDate`() {
        disconnectedContext().use { kontekst ->
            val serialiserer = kontekst.getBean<RedisSerializer<Any>>()
            val ytelse =
                Ytelse().apply {
                    type = "Arbeidsavklaringspenger"
                    fom = LocalDate.of(2026, 1, 1)
                    tom = LocalDate.of(2026, 12, 31)
                    datoKravMottatt = LocalDate.of(2025, 12, 1)
                }
            val svar = YtelseskontraktResponse(listOf(ytelse))

            val gjenopprettet =
                assertInstanceOf(
                    YtelseskontraktResponse::class.java,
                    serialiserer.deserialize(serialiserer.serialize(svar)),
                )
            val gjenopprettetYtelse = gjenopprettet.ytelser.single()
            assertEquals(ytelse.type, gjenopprettetYtelse.type)
            assertEquals(ytelse.fom, gjenopprettetYtelse.fom)
            assertEquals(ytelse.tom, gjenopprettetYtelse.tom)
            assertEquals(ytelse.datoKravMottatt, gjenopprettetYtelse.datoKravMottatt)
        }
    }

    @Test
    fun `serializer round trips oppfolgingskontrakt with LocalDate`() {
        disconnectedContext().use { kontekst ->
            val serialiserer = kontekst.getBean<RedisSerializer<Any>>()
            val svar =
                OppfolgingskontraktResponse().apply {
                    vedtaksdato = LocalDate.of(2026, 1, 1)
                }

            val gjenopprettet =
                assertInstanceOf(
                    OppfolgingskontraktResponse::class.java,
                    serialiserer.deserialize(serialiserer.serialize(svar)),
                )
            assertEquals(svar.vedtaksdato, gjenopprettet.vedtaksdato)
        }
    }

    @Test
    fun `redis cache unwraps deserialized null sentinel`() {
        disconnectedContext().use { kontekst ->
            val serialiserer = kontekst.getBean<RedisSerializer<Any>>()
            val skriver = Mockito.mock(RedisCacheWriter::class.java)
            Mockito.`when`(skriver.get("nullCache", "nullCache::key".toByteArray())).thenReturn(serialiserer.serialize(NullValue.INSTANCE))
            val cacher =
                RedisCacheManager
                    .builder(skriver)
                    .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(serialiserer),
                        ),
                    ).build()
            cacher.afterPropertiesSet()
            val treff = requireNotNull(cacher.getCache("nullCache")?.get("key"))
            assertNull(treff.get())
        }
    }

    @Test
    fun `serializer rejects disallowed types at root and inside collections`() {
        disconnectedContext().use { kontekst ->
            val serialiserer = kontekst.getBean<RedisSerializer<Any>>()
            for (payload in listOf(
                """{"@class":"java.io.File","path":"/tmp/not-allowed"}""",
                """["java.util.ArrayList",[{"@class":"java.io.File","path":"/tmp/not-allowed"}]]""",
                """{"@class":"no.nav.modiapersonoversikt.infrastructure.cache.ValkeyCacheUnitTest${'$'}CacheProbe"}""",
            )) {
                assertThrows(SerializationException::class.java) {
                    serialiserer.deserialize(payload.toByteArray())
                }
            }
            assertThrows(SerializationException::class.java) {
                serialiserer.deserialize(serialiserer.serialize(java.util.UUID.randomUUID()))
            }
        }
    }

    @Test
    fun `only read and write connectivity failures bypass cache`() {
        val metrikker = SimpleMeterRegistry()
        val feilhandterer = ValkeyCacheErrorHandler(metrikker)
        val cache = CaffeineCache("test", Caffeine.newBuilder().build())
        val utilgjengelig = RedisConnectionFailureException("unavailable")

        feilhandterer.handleCacheGetError(utilgjengelig, cache, "key")
        feilhandterer.handleCachePutError(utilgjengelig, cache, "key", "value")
        assertEquals(1.0, metrikker.counter("valkey_cache_bypass_total", "cache", "test", "operation", "get").count())
        assertEquals(1.0, metrikker.counter("valkey_cache_bypass_total", "cache", "test", "operation", "put").count())
        assertThrows(RedisConnectionFailureException::class.java) { feilhandterer.handleCacheEvictError(utilgjengelig, cache, "key") }
        assertThrows(RedisConnectionFailureException::class.java) { feilhandterer.handleCacheClearError(utilgjengelig, cache) }
        assertThrows(SerializationException::class.java) {
            feilhandterer.handleCacheGetError(SerializationException("bad value"), cache, "key")
        }
    }

    private fun disconnectedContext(): AnnotationConfigApplicationContext {
        val kontekst = AnnotationConfigApplicationContext()
        TestPropertyValues
            .of(
                "cache.backend=valkey",
                "cache.valkey.host=localhost",
                "cache.valkey.port=1",
                "cache.valkey.username=default",
                "cache.valkey.password=test-only",
                "cache.valkey.ssl=false",
            ).applyTo(kontekst)
        kontekst.register(CacheConfig::class.java, CacheProbe::class.java)
        kontekst.registerBean(MeterRegistry::class.java, Supplier { SimpleMeterRegistry() })
        kontekst.refresh()
        return kontekst
    }

    open class CacheProbe {
        val kall = AtomicInteger()

        @Cacheable(cacheNames = ["pdlCache"])
        open fun person(id: String): String {
            kall.incrementAndGet()
            return id
        }

        @Cacheable(cacheNames = ["pdlCache"])
        open fun other(id: String): List<String> = listOf(id)

        @Cacheable(cacheNames = ["pdlCache"])
        open fun broken(id: String): String = throw IllegalArgumentException("Source rejected request")
    }

    data class NorskArgument(
        val årsak: String,
    )
}
