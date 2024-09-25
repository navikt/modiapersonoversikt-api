package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class VeiledersTemaPip(
    private val ansattService: AnsattService,
) : Kabac.PolicyInformationPoint<Set<String>> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Set<String>> {
        override val key = Key<Set<String>>(VeiledersTemaPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Set<String> =
        ansattService.hentAnsattFagomrader(
            ident = ctx.getValue(NavIdentPip).get(),
            enhet = ctx.getValue(CommonAttributes.ENHET).get(),
        )
}
