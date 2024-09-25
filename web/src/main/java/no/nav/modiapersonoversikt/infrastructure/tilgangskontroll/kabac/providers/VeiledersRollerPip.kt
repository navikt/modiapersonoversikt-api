package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class VeiledersRollerPip(
    private val ansattService: AnsattService,
) : Kabac.PolicyInformationPoint<RolleListe> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<RolleListe> {
        override val key = Key<RolleListe>(VeiledersRollerPip)
    }

    override fun provide(ctx: EvaluationContext): RolleListe =
        ansattService
            .hentVeilederRoller(ctx.getValue(NavIdentPip))
}
