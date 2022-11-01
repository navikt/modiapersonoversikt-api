package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersDiskresjonskodePip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

object TilgangTilBrukerMedKode7Policy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilBrukerMedKode7Policy)
    private val kode7Roller = RolleListe(
        "0000-ga-fortrolig_adresse",
        "0000-ga-gosys_kode7",
        "0000-ga-pensjon_kode7"
    )

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        if (kode7Roller.hasIntersection(veilederRoller)) {
            return Decision.Permit()
        }
        val diskresjonskode = ctx.getValue(BrukersDiskresjonskodePip)

        return if (diskresjonskode == BrukersDiskresjonskodePip.Kode.KODE7) {
            Decision.Deny("Veileder har ikke tilgang til kode7", DenyCauseCode.FP2_KODE7)
        } else {
            Decision.NotApplicable()
        }
    }
}
