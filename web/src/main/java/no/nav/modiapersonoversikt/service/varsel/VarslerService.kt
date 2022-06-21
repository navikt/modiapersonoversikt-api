package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr
import java.time.ZonedDateTime

interface VarslerService {
    fun hentLegacyVarsler(fnr: Fnr): List<Varsel>
    fun hentAlleVarsler(fnr: Fnr): List<UnifiedVarsel>

    interface UnifiedVarsel

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
