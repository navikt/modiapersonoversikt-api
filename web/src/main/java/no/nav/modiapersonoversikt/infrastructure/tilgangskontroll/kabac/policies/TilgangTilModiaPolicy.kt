package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersEnheterPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

internal object TilgangTilModiaPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilModiaPolicy)
    private val modiaRoller =
        RolleListe("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)
        val veiledersEnheter: List<EnhetId> by lazy { ctx.getValue(VeiledersEnheterPip) }

        return if (modiaRoller.intersect(veilederRoller).isEmpty()) {
            Decision.Deny("Veileder har ikke tilgang til modia", DenyCauseCode.AD_ROLLE)
        } else if (veiledersEnheter.isEmpty()) {
            Decision.Deny("Veileder har ikke tilgang til noen enheter", DenyCauseCode.INGEN_ENHETER)
        } else {
            Decision.Permit()
        }
    }
}
