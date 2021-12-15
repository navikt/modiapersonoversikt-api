package no.nav.modiapersonoversikt.infrastructure

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.utils.fn.UnsafeRunnable
import no.nav.common.utils.fn.UnsafeSupplier
import java.util.*

object AuthContextUtils {
    private val authContextHolder = AuthContextHolderThreadLocal.instance()

    @JvmStatic
    fun getIdent(): Optional<String> = authContextHolder.subject

    @JvmStatic
    fun getToken(): Optional<String> = authContextHolder.idTokenString

    @JvmStatic
    fun getClaims(): Optional<JWTClaimsSet> = authContextHolder.idTokenClaims

    @JvmStatic
    fun getContext(): Optional<AuthContext> = authContextHolder.context

    @JvmStatic
    fun requireIdent(): String = authContextHolder.requireSubject()

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
}
