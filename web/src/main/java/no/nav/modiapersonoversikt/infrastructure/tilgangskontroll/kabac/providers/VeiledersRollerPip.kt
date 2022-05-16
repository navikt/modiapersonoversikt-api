package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class VeiledersRollerPip(private val ldap: LDAPService) : Kabac.PolicyInformationPoint<Set<String>> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<Set<String>> {
        override val key = Key<Set<String>>(VeiledersRollerPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Set<String> {
        return ldap.hentRollerForVeileder(ctx.getValue(NavIdentPip)).toSet()
    }
}
