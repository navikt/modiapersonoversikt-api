package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.utils.Key

object TilgangTilInnkrevingskravPolicy : Kabac.Policy {
    private val innkrevingskravRoller = RolleListe("0000-ga-modia-innkrevingskrav")

    override val key: Key<Kabac.Policy> = Key(TilgangTilInnkrevingskravPolicy)

    override fun evaluate(ctx: Kabac.EvaluationContext): Decision {
        val veiledersRoller = ctx.getValue(VeiledersRollerPip)

        return if (innkrevingskravRoller.hasIntersection(veiledersRoller)) {
            Decision.Permit()
        } else {
            Decision.Deny("Veileder har ikke tilgang til innkrevingskrav", DenyCauseCode.AD_ROLLE)
        }
    }
}
