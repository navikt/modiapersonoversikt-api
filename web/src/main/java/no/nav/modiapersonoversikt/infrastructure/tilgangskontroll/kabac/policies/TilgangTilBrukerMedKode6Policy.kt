package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedKode6Policy : Kabac.Policy {
    private val kode6Roller = setOf("0000-ga-strengt_fortrolig_adresse", "0000-ga-gosys_kode6")

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()

        if (kode6Roller.intersect(veilederRoller).isNotEmpty()) {
            return Kabac.Decision.Permit()
        }
        val diskresjonskode = ctx.getValue(BrukersDiskresjonskodePip)

        return if (diskresjonskode == BrukersDiskresjonskodePip.Kode.KODE6) {
            Kabac.Decision.Deny("Veileder har ikke tilgang til kode6")
        } else {
            Kabac.Decision.NotApplicable()
        }
    }
}
