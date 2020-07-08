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
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavn
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TjenestekallLogger
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.net.URL
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


class PdlOppslagServiceImpl : PdlOppslagService {
    private val log = LoggerFactory.getLogger(PdlOppslagServiceImpl::class.java)
    private val OPPSLAG_URL = getEnvironmentUrl()
    private val graphQLClient = GraphQLClient(
            url = URL(OPPSLAG_URL)
    )

    @Inject
    private lateinit var stsService: SystemUserTokenProvider

    override fun hentPerson(fnr: String): HentPerson.Person? = doRequest(fnr) { pdlFnr, httpHeaders ->
        HentPerson(graphQLClient).execute(HentPerson.Variables(pdlFnr, false), httpHeaders)
    }?.data?.hentPerson

    override fun hentNavn(fnr: String): HentNavn.Person? = doRequest(fnr) { pdlFnr, httpHeaders ->
        HentNavn(graphQLClient).execute(HentNavn.Variables(pdlFnr, false), httpHeaders)
    }?.data?.hentPerson

    override fun hentIdent(fnr: String): HentIdent.Identliste? = doRequest(fnr) { pdlFnr, httpHeaders ->
        HentIdent(graphQLClient).execute(HentIdent.Variables(pdlFnr, false), httpHeaders)
    }?.data?.hentIdenter

    private fun <T> doRequest(fnr: String, request: suspend (String, HttpRequestBuilder.() -> Unit) -> GraphQLResponse<T>): GraphQLResponse<T>? {
        val uuid = UUID.randomUUID()
        try {
            val pdlFnr = PdlSyntetiskMapper.mapFnrTilPdl(fnr)
            TjenestekallLogger.info("PDL-request: $uuid", mapOf(
                    "ident" to pdlFnr,
                    "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
            ))
            val consumerOidcToken: String = stsService.systemUserAccessToken
            val veilederOidcToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }
            return runBlocking {
                val response: GraphQLResponse<T> = request(pdlFnr) {
                    header(NAV_CALL_ID_HEADER, uuid.toString())
                    header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                    header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                }
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
}

private fun getEnvironmentUrl(): String {
    if ("p".equals(EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY))) {
        return "https://pdl-api.nais.adeo.no/graphql"
    } else {
        return "https://pdl-api.nais.preprod.local/graphql"
    }
}
