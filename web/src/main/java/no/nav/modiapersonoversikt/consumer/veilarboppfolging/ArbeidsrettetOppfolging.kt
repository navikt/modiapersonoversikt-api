package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Veileder

object ArbeidsrettetOppfolging {
    interface Service {
        fun hentOppfolgingsinfo(fodselsnummer: Fnr): Info

        fun ping()
    }

    data class Info(
        val erUnderOppfolging: Boolean,
        val erManuell: Boolean,
        val veileder: Veileder?,
        val oppfolgingsenhet: OppfolgingsEnhet?,
    )

    data class OppfolgingsEnhet(
        val enhetId: String,
        val navn: String,
    )
}
