package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe

class VeiledersRollerPip(private val ldap: LDAPService) : Kabac.PolicyInformationPoint<RolleListe> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<RolleListe> {
        override val key = Key<RolleListe>(VeiledersRollerPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): RolleListe {
        return ldap
            .hentRollerForVeileder(ctx.getValue(NavIdentPip))
            .map { it.lowercase() }
            .let(::RolleListe)
    }
}
