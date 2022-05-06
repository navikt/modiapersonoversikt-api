package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedSkjermingPolicy : Kabac.Policy {
    private val skjermingRoller = setOf("0000-ga-gosys_utvidet", "0000-ga-pensjon_utvidet")

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.requireValue(VeiledersRollerPip)

        if (skjermingRoller.union(veilederRoller).isNotEmpty()) {
            return Kabac.Decision.Permit()
        }
        val diskresjonskode = ctx.requireValue(BrukersDiskresjonskodePip)

        return if (diskresjonskode == BrukersDiskresjonskodePip.Kode.KODE6) {
            Kabac.Decision.Deny("Veileder har ikke tilgang til kode6")
        } else {
            Kabac.Decision.NotApplicable()
        }
    }
}
