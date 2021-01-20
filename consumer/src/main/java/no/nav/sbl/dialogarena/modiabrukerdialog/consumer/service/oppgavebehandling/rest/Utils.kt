package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Utils {
    const val DEFAULT_ENHET = "4100"
    const val STORD_ENHET = "4842"
    const val KONTAKT_NAV = "KNA"
    const val SPORSMAL_OG_SVAR = "SPM_OG_SVR"

    fun endretAvEnhet(temagruppe: Temagruppe?, valgtEnhet: String?): String {
        return if (temagruppe == null) {
            DEFAULT_ENHET
        } else if (temagruppe == FMLI && valgtEnhet == STORD_ENHET) {
            STORD_ENHET
        } else if (listOf(ARBD, HELSE, FMLI, FDAG, ORT_HJE, PENS, UFRT, PLEIEPENGERSY, UTLAND).contains(temagruppe)) {
            DEFAULT_ENHET
        } else {
            valgtEnhet ?: throw IllegalStateException("Kunne ikke utlede endretAvEnhet gitt $temagruppe og $valgtEnhet")
        }
    }

    fun beskrivelseInnslag(ident: String, navn: String, enhet: String?, innhold: String?): String {
        return String.format(
            "--- %s %s (%s, %s) ---\n%s",
            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
            navn,
            ident,
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
}
