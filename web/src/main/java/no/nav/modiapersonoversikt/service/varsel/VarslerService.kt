package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.time.ZonedDateTime

@CacheConfig(cacheNames = ["varslingCache"], keyGenerator = "userkeygenerator")
interface VarslerService {
    @Cacheable
    fun hentLegacyVarsler(fnr: Fnr): List<Varsel>

    @Cacheable
    fun hentAlleVarsler(fnr: Fnr): Result

    interface UnifiedVarsel

    data class Result(val feil: List<String>, val varsler: List<UnifiedVarsel>)
    data class VarselMelding(
        val kanal: String?,
        val innhold: String?,
        val mottakerInformasjon: String?,
        val utsendingsTidspunkt: ZonedDateTime?,
        val feilbeskrivelse: String?,
        val epostemne: String?,
        val url: String?,
        val erRevarsel: Boolean?,
    )

    data class Varsel(
        val varselType: String?,
        val mottattTidspunkt: ZonedDateTime?,
        val meldingListe: List<VarselMelding>?,
        val erRevarsling: Boolean,
    ) : UnifiedVarsel
}
