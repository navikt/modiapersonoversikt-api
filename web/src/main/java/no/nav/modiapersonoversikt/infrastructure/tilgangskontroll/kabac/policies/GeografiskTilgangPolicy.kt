package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

@Suppress("FoldInitializerAndIfToElvis")
object GeografiskTilgangPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(GeografiskTilgangPolicy)
    private val nasjonalTilgangRoller =
        RolleListe(
            "0000-ga-gosys_nasjonal",
            "0000-ga-gosys_utvidbar_til_nasjonal",
            "0000-ga-pensjon_nasjonal_u_logg",
            "0000-ga-pensjon_nasjonal_m_logg",
        )

    private val regionalTilgangRoller =
        RolleListe(
            "0000-ga-gosys_regional",
            "0000-ga-gosys_utvidbar_til_regional",
        )

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)
        if (nasjonalTilgangRoller.hasIntersection(veilederRoller)) {
            return Decision.Permit()
        }

        val brukersEnhet: EnhetId? = ctx.getValue(BrukersEnhetPip)
        if (brukersEnhet == null) {
            return Decision.Permit()
        }

        val veiledersEnheter: List<EnhetId> = ctx.getValue(VeiledersEnheterPip)
        if (veiledersEnheter.contains(brukersEnhet)) {
            return Decision.Permit()
        }

        if (regionalTilgangRoller.hasIntersection(veilederRoller)) {
            val brukersRegion: EnhetId? = ctx.getValue(BrukersRegionEnhetPip)
            val veiledersRegioner: List<EnhetId> = ctx.getValue(VeiledersRegionEnheterPip)

            if (veiledersRegioner.contains(brukersRegion)) {
                return Decision.Permit()
            }
        }

        return Decision.Deny("Veileder har ikke tilgang til bruker basert på geografisk tilgang", DenyCauseCode.FP4_GEOGRAFISK)
    }
}
