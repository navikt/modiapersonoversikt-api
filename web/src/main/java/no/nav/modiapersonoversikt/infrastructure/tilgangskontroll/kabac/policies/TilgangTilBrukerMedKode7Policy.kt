package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedKode7Policy : Kabac.Policy {
    private val kode7Roller = setOf("0000-ga-fortrolig_adresse", "0000-ga-gosys_kode7")

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()

        if (kode7Roller.intersect(veilederRoller).isNotEmpty()) {
            return Kabac.Decision.Permit()
        }
        val diskresjonskode = ctx.getValue(BrukersDiskresjonskodePip)

        return if (diskresjonskode == BrukersDiskresjonskodePip.Kode.KODE7) {
            Kabac.Decision.Deny("Veileder har ikke tilgang til kode7")
        } else {
            Kabac.Decision.NotApplicable()
        }
    }
}
