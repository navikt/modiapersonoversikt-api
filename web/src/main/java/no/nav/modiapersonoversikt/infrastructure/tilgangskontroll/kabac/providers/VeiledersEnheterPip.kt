package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class VeiledersEnheterPip(private val ansattService: AnsattService) : Kabac.PolicyInformationPoint<List<EnhetId>> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<List<EnhetId>> {
        override val key = Key<List<EnhetId>>(VeiledersEnheterPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): List<EnhetId> {
        val ident = ctx.getValue(NavIdentPip)
        return ansattService.hentEnhetsliste(ident).map { EnhetId(it.enhetId) }
    }
}
