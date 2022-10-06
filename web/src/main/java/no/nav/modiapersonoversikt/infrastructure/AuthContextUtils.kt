package no.nav.modiapersonoversikt.infrastructure

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.auth.Constants.AAD_NAV_IDENT_CLAIM
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.fn.UnsafeRunnable
import no.nav.common.utils.fn.UnsafeSupplier
import java.util.*

object AuthContextUtils {
    private val authContextHolder = AuthContextHolderThreadLocal.instance()

    @JvmStatic
    fun getNavIdent(): Optional<NavIdent> = authContextHolder.navIdent

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
    fun requireClaims(): JWTClaimsSet = authContextHolder.requireIdTokenClaims()

    @JvmStatic
    fun requireContext(): AuthContext = authContextHolder.requireContext()

    @JvmStatic
    fun withContext(authContext: AuthContext?, block: UnsafeRunnable) = authContextHolder.withContext(authContext, block)

    @JvmStatic
    fun <T> withContext(authContext: AuthContext?, block: UnsafeSupplier<T>): T = authContextHolder.withContext(authContext, block)

    @JvmStatic
    fun getTokenType(): TokenType {
        val claims = requireClaims()
        return when (claims.getStringClaim(AAD_NAV_IDENT_CLAIM)) {
            null -> TokenType.OPEN_AM
            else -> TokenType.AZURE_AD
        }
    }

    enum class TokenType {
        OPEN_AM, AZURE_AD
    }
}
