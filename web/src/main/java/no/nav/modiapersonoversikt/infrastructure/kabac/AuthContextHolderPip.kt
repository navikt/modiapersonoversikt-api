package no.nav.modiapersonoversikt.infrastructure.kabac

import no.nav.common.auth.context.AuthContextHolder
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

object AuthContextHolderPip : Kabac.Provider<AuthContextHolder> {
    override val key = Key<AuthContextHolder>(AuthContextHolderPip::class.java.simpleName)

    context(EvaluationContext) override fun provide(): AuthContextHolder {
        return AuthContextHolderThreadLocal.instance()
    }
}

object NavIdentPip : Kabac.Provider<NavIdent> {
    override val key = Key<NavIdent>(NavIdentPip::class.java.simpleName)

    context(EvaluationContext) override fun provide(): NavIdent? {
        val auth: AuthContextHolder = requireValue(AuthContextHolderPip)
        return auth.navIdent.orElse(null)
    }
}

class AttributeProvider<T : Any>(override val key: Key<T>, private val value: T?) : Kabac.Provider<T> {
    context(EvaluationContext) override fun provide(): T? = value
}
object CommonAttributes {
    val FNR = Key<String>("no.nav.common.kabac.fnr")
    val ENV = Key<String>("no.nav.common.kabac.environment")
}

class AktorIdPip(val pdl: PdlOppslagService) : Kabac.Provider<String> {
    companion object : Kabac.ProviderKey<String> {
        override val key = Key<String>(AktorIdPip::class.java.simpleName)
    }

    override val key = AktorIdPip.key
    context(EvaluationContext) override fun provide(): String? {
        val fnr = requireValue(CommonAttributes.FNR)
        return pdl.hentAktorId(fnr)
    }
}