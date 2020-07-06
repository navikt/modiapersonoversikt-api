package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory

internal data class TpsFnr(val tpsIdent: String)
internal data class PdlFnr(val pdlIdent: String)

internal val enableMapper = "p" != EnvironmentUtils.getRequiredProperty(ByEnvironmentStrategy.ENVIRONMENT_PROPERTY)
internal val log = LoggerFactory.getLogger(PdlSyntetiskFnrMapper::class.java)

object PdlSyntetiskFnrMapper {
    // TODO TpsFnr skal kunne være for fiktive personer (e.g. Testfamilien).
    // TODO PdlFnr er genererte syntetiske fnr/personer.
    private val fnrmap : Map<TpsFnr, List<PdlFnr>> = mapOf(
            TpsFnr("10108000398") to listOf(PdlFnr("12028213016")), // Aremark Testfamilien
            TpsFnr("06128074978") to listOf(), // Trøgstad Testfamilien
            TpsFnr("07063000250") to listOf() // Moss Testfamilien
    )

    init {
        log.info("Starting PdlFnrMapper, enabled: $enableMapper")
    }

    fun mapTilPdl(fnr: String) : String =
            if (enableMapper) {
                log.error("Brukte PdlSyntetiskFnrMapper, denne skal aldri vises i produksjon")
                fnrmap[TpsFnr(fnr)]?.get(0)?.pdlIdent ?: fnr
            } else {
                fnr
            }
}
