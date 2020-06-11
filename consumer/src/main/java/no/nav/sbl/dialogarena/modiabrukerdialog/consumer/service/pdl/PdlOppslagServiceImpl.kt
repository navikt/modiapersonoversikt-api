package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated.HentNavn
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.net.URL
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.HttpHeaders
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CoroutineHeaderFeature {
    data class Headers(val headers: Map<String, String>) : AbstractCoroutineContextElement(Headers) {
        companion object Key : CoroutineContext.Key<Headers>
    }

    companion object Feature : HttpClientFeature<Void, CoroutineHeaderFeature> {
        override val key: AttributeKey<CoroutineHeaderFeature> = AttributeKey("CoroutineHeader")

        override fun prepare(block: Void.() -> Unit) = CoroutineHeaderFeature()
        override fun install(feature: CoroutineHeaderFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                val headers = coroutineContext[Headers.Key]?.headers ?: emptyMap()
                headers.forEach { (key, value) ->
                    context.header(key, value)
                }
            }
        }
    }
}

class PdlOppslagServiceImpl : PdlOppslagService {
    private val log = LoggerFactory.getLogger(PdlOppslagServiceImpl::class.java)
    private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
    private val OPPSLAG_URL = getEnvironmentUrl()
    private val graphQLClient = GraphQLClient(
            url = URL(OPPSLAG_URL),
            config = {
                install(CoroutineHeaderFeature)
            }
    )

    @Inject
    private lateinit var stsService: SystemUserTokenProvider

    override fun hentPerson(fnr: String): HentPerson.Person? = doRequest(fnr) {
        HentPerson(graphQLClient).execute(HentPerson.Variables(fnr, false)).data?.hentPerson
    }


    override fun hentNavn(fnr: String): HentNavn.Person? = doRequest(fnr) {
        HentNavn(graphQLClient).execute(HentNavn.Variables(fnr, false)).data?.hentPerson
    }

    private fun <T> doRequest(ident: String, request: suspend () -> T): T? {
        val uuid = UUID.randomUUID()
        return try {
            tjenestekallLogg.info("""
                PDL-request: $uuid
                ------------------------------------------------------------------------------------
                    ident: $ident
                    callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
                ------------------------------------------------------------------------------------
            """.trimIndent())
            val consumerOidcToken: String = stsService.systemUserAccessToken
            val veilederOidcToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

            val headerValues = mapOf(
                    RestConstants.NAV_CALL_ID_HEADER to uuid.toString(),
                    HttpHeaders.AUTHORIZATION to RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + veilederOidcToken,
                    RestConstants.NAV_CONSUMER_TOKEN_HEADER to RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken,
                    RestConstants.TEMA_HEADER to RestConstants.ALLE_TEMA_HEADERVERDI
            )

            runBlocking(CoroutineHeaderFeature.Headers(headerValues)) {
                request()
            }
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot PDL (ID: $uuid)", exception)
            tjenestekallLogg.error("""
                PDL-response: $uuid
                ------------------------------------------------------------------------------------
                    exception:
                    $exception
                ------------------------------------------------------------------------------------
            """.trimIndent())
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
