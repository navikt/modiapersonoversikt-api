package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.commondomain.Temagruppe.*
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

object Utils {
    private const val DEFAULT_ENHET = "4100"
    private const val STORD_ENHET = "4842"
    const val KONTAKT_NAV = "KNA"
    const val SPORSMAL_OG_SVAR = "SPM_OG_SVR"

    fun defaultEnhetGittTemagruppe(temagruppe: Temagruppe?, valgtEnhet: String?): String {
        return if (temagruppe == null) {
            DEFAULT_ENHET
        } else if (temagruppe == FMLI && valgtEnhet == STORD_ENHET) {
            STORD_ENHET
        } else if (listOf(ARBD, HELSE, FMLI, FDAG, ORT_HJE, PENS, UFRT, PLEIEPENGERSY, UTLAND, OVRG).contains(temagruppe)) {
            DEFAULT_ENHET
        } else {
            valgtEnhet ?: throw IllegalStateException("Kunne ikke utlede endretAvEnhet gitt $temagruppe og ingen enhet")
        }
    }

    fun beskrivelseInnslag(
        ident: NavIdent,
        navn: String,
        enhet: String?,
        innhold: String?,
        clock: Clock
    ): String {
        return String.format(
            "--- %s %s (%s, %s) ---\n%s",
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now(clock)),
            navn,
            ident.get(),
            enhet,
            innhold
        )
    }

    fun leggTilBeskrivelse(gammelBeskrivelse: String?, leggTil: String): String {
        return if (gammelBeskrivelse.isNullOrBlank()) {
            leggTil
        } else {
            "$leggTil\n\n$gammelBeskrivelse"
        }
    }

    /**
     * Maks 50 om man bruker userToken mot oppgave.
     * En liten off-by-one bug i oppgave gjør at vi per nå må sette den til 49
     */
    const val OPPGAVE_MAX_LIMIT: Long = 49
    fun <RESPONSE, DATA> paginering(
        total: (response: RESPONSE) -> Long,
        data: (response: RESPONSE) -> List<DATA>,
        action: (offset: Long) -> RESPONSE
    ): List<DATA> {
        var page: Long = 0
        val buffer = mutableListOf<DATA>()

        do {
            val response: RESPONSE = action(page * OPPGAVE_MAX_LIMIT)
            val inTotal: Long = total(response)
            val maxPage = ceil(inTotal.toDouble() / OPPGAVE_MAX_LIMIT).toInt()
            val actionData: List<DATA> = data(response)
            buffer.addAll(actionData)
            page++
        } while (page < maxPage)

        return buffer
    }
}
