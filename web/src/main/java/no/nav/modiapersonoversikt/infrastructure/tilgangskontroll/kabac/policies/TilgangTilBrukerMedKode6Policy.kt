package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

object TilgangTilBrukerMedKode6Policy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilBrukerMedKode6Policy)
    private val kode6Roller =
        RolleListe(
            "0000-ga-strengt_fortrolig_adresse",
        )

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        if (kode6Roller.hasIntersection(veilederRoller)) {
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
