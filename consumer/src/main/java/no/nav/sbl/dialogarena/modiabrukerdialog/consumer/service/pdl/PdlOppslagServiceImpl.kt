package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.request.header
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.util.EnvironmentUtils
import java.net.URL
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION

@KtorExperimentalAPI
class PdlOppslagServiceImpl constructor(
        private val stsService: SystemUserTokenProvider,
        private val pdlClient: GraphQLClient<*>
) : PdlOppslagService {
    constructor(stsService: SystemUserTokenProvider) : this(stsService, createClient())

    override fun hentPerson(fnr: String): HentPerson.Person? = runBlocking {
        HentPerson(pdlClient)
                .execute(HentPerson.Variables(fnr), userTokenAuthorizationHeaders)
                .data
                ?.hentPerson
    }

    override fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>? {
        if (fnrs.isEmpty()) {
            emptyMap<String, HentNavnBolk.Navn?>()
        }

        return runBlocking {
            HentNavnBolk(pdlClient).execute(HentNavnBolk.Variables(fnrs), systemTokenAuthorizationHeaders)
        }
                .data
                ?.hentPersonBolk
                ?.fold(mutableMapOf()) { acc, bolkResult ->
                    acc[bolkResult.ident] = bolkResult.person?.navn?.get(0)
                    acc
                }
    }

    override fun hentIdent(fnr: String): HentIdent.Identliste? = runBlocking {
        HentIdent(pdlClient)
                .execute(HentIdent.Variables(fnr), userTokenAuthorizationHeaders)
                .data
                ?.hentIdenter
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserAccessToken
        val userToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + userToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    private val systemTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserAccessToken

        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    companion object {
        private val pdlApiUrl: URL = EnvironmentUtils.getRequiredProperty("PDL_API_URL").let(::URL)

        fun createClient() = PdlClient(pdlApiUrl) { variables ->
            when (variables) {
                null -> emptyList<String>() to variables
                is HentPerson.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    listOf(ident) to HentPerson.Variables(ident)
                }
                is HentNavnBolk.Variables -> {
                    val identer = variables.identer.map(PdlSyntetiskMapper::mapFnrTilPdl)
                    identer to HentNavnBolk.Variables(identer)
                }
                is HentIdent.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    listOf(ident) to HentIdent.Variables(ident)
                }
                else -> throw IllegalStateException("Unrecognized graphql variables type: ${variables.javaClass.simpleName}")
            }
        }
    }
}

