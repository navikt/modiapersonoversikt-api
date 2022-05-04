package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class VeilederRollerPip(private val ldap: LDAPService) : Kabac.AttributeProvider<List<String>> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<List<String>> {
        override val key = Key<List<String>>(VeilederRollerPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): List<String> {
        return ldap.hentRollerForVeileder(ctx.requireValue(NavIdentPip))
    }
}
