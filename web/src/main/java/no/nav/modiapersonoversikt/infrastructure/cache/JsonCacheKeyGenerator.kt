package no.nav.modiapersonoversikt.infrastructure.cache

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.cache.interceptor.KeyGenerator
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.cfg.EnumFeature
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.lang.reflect.Method

/**
 * Nøklene er JSON i klartekst: identifikatorer som fnr og Nav-ident kan leses i Valkey og i
 * `db.statement` på Redis-spans. Hele argumentene tas med (ikke hashCode), så ulike kall aldri deler nøkkel.
 */
internal class JsonCacheKeyGenerator(
    private val inkluderBruker: Boolean,
) : KeyGenerator {
    private val mapper =
        jacksonMapperBuilder()
            .accessorNaming(DefaultAccessorNamingStrategy.Provider().withFirstCharAcceptance(true, true))
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build()

    override fun generate(
        target: Any,
        method: Method,
        vararg params: Any?,
    ): Any {
        val signatur =
            listOf(
                AopProxyUtils.ultimateTargetClass(target).name,
                method.name,
                method.parameterTypes.map { it.name },
                if (inkluderBruker) AuthContextUtils.getIdent().orElse("uauthentisert") else null,
                params.toList(),
            )
        return mapper.writeValueAsString(signatur)
    }
}
