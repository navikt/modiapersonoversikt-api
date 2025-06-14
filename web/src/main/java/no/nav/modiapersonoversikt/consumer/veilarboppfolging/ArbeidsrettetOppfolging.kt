package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Veileder

object ArbeidsrettetOppfolging {
    interface Service {
        fun hentOppfolgingsinfo(fodselsnummer: Fnr): Info

        fun hentOppfolgingStatus(fodselsnummer: Fnr): Status

        fun ping()
    }

    data class Status(
        val underOppfolging: Boolean,
        val erManuell: Boolean,
    )

    data class Info(
        val erUnderOppfolging: Boolean,
        val erManuell: Boolean,
        val veileder: Veileder?,
        val oppfolgingsenhet: OppfolgingsEnhet?,
    )

    data class EnhetOgVeileder(
        val oppfolgingsenhet: OppfolgingsEnhet?,
        val veilederId: String?,
    )

    data class OppfolgingsEnhet(
        val enhetId: String,
        val navn: String,
    )
}
