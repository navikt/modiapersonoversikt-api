package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService

class VeiledersEnheterPip(private val ansattService: AnsattService) : Kabac.AttributeProvider<List<EnhetId>> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<List<EnhetId>> {
        override val key = Key<List<EnhetId>>(VeiledersEnheterPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): List<EnhetId> {
        val ident = ctx.requireValue(NavIdentPip)
        return ansattService.hentEnhetsliste(ident).map { EnhetId(it.enhetId) }
    }
}
