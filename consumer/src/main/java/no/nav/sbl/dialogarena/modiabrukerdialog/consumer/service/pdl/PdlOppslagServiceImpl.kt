package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TjenestekallLogger
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.net.URL
import java.util.*
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION

typealias HeadersBuilder = HttpRequestBuilder.() -> Unit
typealias Headers = (UUID) -> HeadersBuilder

class PdlOppslagServiceImpl(
        private val stsService : SystemUserTokenProvider,
        private val graphQLClient: GraphQLClient<*>
) : PdlOppslagService {
    private val log = LoggerFactory.getLogger(PdlOppslagServiceImpl::class.java)

    constructor(stsService: SystemUserTokenProvider) : this(stsService, createMockGraphQlClient())

    override fun hentPerson(fnr: String): HentPerson.Person? = prepareRequest(fnr)
            .executeRequest { HentPerson(graphQLClient).execute(HentPerson.Variables(it.fnr), it.headers) }
            ?.data?.hentPerson


    override fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>? {
        if (fnrs.isEmpty()) {
            return emptyMap()
        }


        val response = prepareRequest(fnrs, ::systemTokenHeaders)
                .executeRequest { HentNavnBolk(graphQLClient).execute(HentNavnBolk.Variables(it.fnr), it.headers) }

        return response?.data?.hentPersonBolk
                ?.fold(mutableMapOf()) { acc, bolkResult ->
                    acc[bolkResult.ident] = bolkResult.person?.navn?.get(0)
                    acc
                }
    }

    override fun hentIdent(fnr: String): HentIdent.Identliste? = prepareRequest(fnr)
            .executeRequest { HentIdent(graphQLClient).execute(HentIdent.Variables(it.fnr), it.headers) }
            ?.data?.hentIdenter

    private data class RequestContext<V>(val uuid: UUID, val fnr: V, val headers: HeadersBuilder)

    private fun prepareRequest(fnr: String, headers: Headers = ::userTokenHeaders) = UUID.randomUUID()
            .let { RequestContext(it, PdlSyntetiskMapper.mapFnrTilPdl(fnr), headers(it)) }

    private fun prepareRequest(fnrs: List<String>, headers: Headers = ::userTokenHeaders) = UUID.randomUUID()
            .let { RequestContext(it, fnrs.map(PdlSyntetiskMapper::mapFnrTilPdl), headers(it)) }

    private fun <T, V> RequestContext<V>.executeRequest(request: suspend (RequestContext<V>) -> GraphQLResponse<T>): GraphQLResponse<T>? {
        val context = this
        return try {
            runBlocking {
                TjenestekallLogger.info("PDL-request: $uuid", mapOf(
                        "ident" to context.fnr,
                        "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
                ))

                val response: GraphQLResponse<T> = request(context)

                val tjenestekallFelt = mapOf(
                        "data" to response.data,
                        "errors" to response.errors,
                        "extensions" to response.extensions
                )

                if (response.errors.isNullOrEmpty()) {
                    TjenestekallLogger.info("PDL-response: $uuid", tjenestekallFelt)
                    return@runBlocking response
                } else {
                    TjenestekallLogger.error("PDL-response: $uuid", tjenestekallFelt)
                    return@runBlocking null
                }
            }
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot PDL (ID: $uuid)", exception)
            TjenestekallLogger.error("PDL-response: $uuid", mapOf(
                    "exception" to exception
            ))
            return null
        }
    }

    private fun userTokenHeaders(uuid: UUID): HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserAccessToken
        val userToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

        header(NAV_CALL_ID_HEADER, uuid.toString())
        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + userToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    private fun systemTokenHeaders(uuid: UUID): HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserAccessToken

        header(NAV_CALL_ID_HEADER, uuid.toString())
        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

}

private fun createMockGraphQlClient(): GraphQLClient<*> {
    val url = if ("p" == EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY)) {
        "https://pdl-api.nais.adeo.no/graphql"
    } else {
        "https://pdl-api.nais.preprod.local/graphql"
    }
    return GraphQLClient(url = URL(url))
}

