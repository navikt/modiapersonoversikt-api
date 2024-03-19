package no.nav.modiapersonoversikt.infrastructure

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.fn.UnsafeRunnable
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import java.util.*

object AuthContextUtils {
    private val authContextHolder = AuthContextHolderThreadLocal.instance()

    @JvmStatic
    fun getIdent(): Optional<String> = authContextHolder.navIdent.map { it.get() }

    @JvmStatic
    fun getToken(): Optional<String> = authContextHolder.idTokenString

    @JvmStatic
    fun getClaims(): Optional<JWTClaimsSet> = authContextHolder.idTokenClaims

    @JvmStatic
    fun getContext(): Optional<AuthContext> = authContextHolder.context

    @JvmStatic
    fun requireNavIdent(): NavIdent = authContextHolder.requireNavIdent()

    @JvmStatic
    fun requireIdent(): String = authContextHolder.requireNavIdent().get()

    @JvmStatic
    fun requireToken(): String = authContextHolder.requireIdTokenString()

    @JvmStatic
    fun requireBoundedClientOboToken(oboTokenProvider: BoundedOnBehalfOfTokenClient): String {
        val token = requireToken()
        return oboTokenProvider.exchangeOnBehalfOfToken(token)
    }

    @JvmStatic
    fun withContext(
        authContext: AuthContext?,
        block: UnsafeRunnable,
    ) = authContextHolder.withContext(authContext, block)

    @JvmStatic
    fun <T> withContext(
        authContext: AuthContext?,
        block: UnsafeSupplier<T>,
    ): T = authContextHolder.withContext(authContext, block)

    @JvmStatic
    fun <T> withContext(
        authContext: AuthContext?,
        block: () -> T,
    ): T = authContextHolder.withContext(authContext, UnsafeSupplier { block() })
}
