package no.nav.modiapersonoversikt.service.pdl

import org.slf4j.LoggerFactory

internal data class TpsFnr(val tpsIdent: String)
internal data class PdlFnr(val pdlIdent: String)

internal val enableMapper = false // "p" != EnvironmentUtils.getOptionalProperty(ByEnvironmentStrategy.ENVIRONMENT_PROPERTY).orElse("p")
internal val log = LoggerFactory.getLogger(PdlSyntetiskMapper::class.java)

object PdlSyntetiskMapper {
    // TODO TpsFnr skal kunne være for fiktive personer (e.g. Testfamilien).
    // TODO PdlFnr er genererte syntetiske fnr/personer.
    private val fnrmap: Map<TpsFnr, List<PdlFnr>> = mapOf(
        TpsFnr("10108000398") to listOf(PdlFnr("19096118867")), // Aremark Testfamilien
        TpsFnr("06128074978") to listOf(), // Trøgstad Testfamilien
        TpsFnr("07063000250") to listOf() // Moss Testfamilien
    )

    init {
        log.info("Starting PdlSyntetiskMapper, enabled: $enableMapper")
    }

    fun mapFnrTilPdl(fnr: String): String =
        if (enableMapper) {
            log.error("Brukte PdlSyntetiskMapper, denne skal aldri vises i produksjon")
            fnrmap[TpsFnr(fnr)]?.firstOrNull()?.pdlIdent ?: fnr
        } else {
            fnr
        }

    fun mapAktorIdFraPdl(aktorId: String): String =
        if (enableMapper) {
            log.error("Brukte PdlSyntetiskMapper, denne skal aldri vises i produksjon")
            if (aktorId == "2004819988162") {
                "1989093374365"
            } else {
                "1000096233942"
            }
        } else {
            aktorId
        }
}
