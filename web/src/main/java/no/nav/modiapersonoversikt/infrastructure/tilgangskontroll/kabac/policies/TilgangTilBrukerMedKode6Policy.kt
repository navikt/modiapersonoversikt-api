package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedKode6Policy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilBrukerMedKode6Policy)
    private val kode6Roller = setOf(
        "0000-ga-strengt_fortrolig_adresse",
        "0000-ga-gosys_kode6",
        "0000-ga-pensjon_kode6"
    )

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        if (kode6Roller.intersect(veilederRoller).isNotEmpty()) {
            return Decision.Permit()
        }
        val diskresjonskode = ctx.getValue(BrukersDiskresjonskodePip)

        return if (diskresjonskode == BrukersDiskresjonskodePip.Kode.KODE6) {
            Decision.Deny("Veileder har ikke tilgang til kode6", DenyCauseCode.FP1_KODE6)
        } else {
            Decision.NotApplicable()
        }
    }
}
