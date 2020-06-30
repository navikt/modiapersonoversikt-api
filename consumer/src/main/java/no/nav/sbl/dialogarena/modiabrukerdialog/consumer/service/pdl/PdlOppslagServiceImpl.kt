package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import com.google.gson.GsonBuilder
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.PdlPersonResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.PdlRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.Variables
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.rest.RestUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import javax.inject.Inject
import javax.ws.rs.client.Entity
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


class PdlOppslagServiceImpl : PdlOppslagService {
    private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
    private val OPPSLAG_URL = getEnvironmentUrl();
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @Inject
    private lateinit var stsService: StsServiceImpl

    override fun hentPerson(fnr: String): PdlPersonResponse? {
        val query = this::class.java.getResource("/pdl/hentPerson.graphql").readText().replace("[\n\r]", "")
        return graphqlRequest(PdlRequest(query, Variables(fnr)))
    }

    private fun graphqlRequest(request: PdlRequest): PdlPersonResponse? {
        val veilederOidcToken = SubjectHandler.getSsoToken().orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }
        val consumerOidcToken = stsService.hentConsumerOidcToken()

        val uuid = UUID.randomUUID()

        tjenestekallLogg.info("""
            PDL-request: $uuid
            ------------------------------------------------------------------------------------
                ident: ${request.variables.ident}
                callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
            ------------------------------------------------------------------------------------
        """.trimIndent())

        val content: String = RestUtils.withClient { client ->
            val response = client.target(OPPSLAG_URL)
                    .request()
                    .header(NAV_PERSONIDENT_HEADER, request.variables.ident)
                    .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                    .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                    .header(OPPLYSNINGSTYPER_HEADER, OPPLYSNINGSTYPER_HEADERVERDI)
                    .post(Entity.json(request))

            val body = response.readEntity(String::class.java)
            tjenestekallLogg.info("""
            PDL-response: $uuid
            ------------------------------------------------------------------------------------
                status: ${response.status} ${response.statusInfo}
                body: $body
            ------------------------------------------------------------------------------------
        """.trimIndent())

            body
        }

        return gson.fromJson(content, PdlPersonResponse::class.java)
    }
}

private fun getEnvironmentUrl(): String {
    if ("p".equals(System.getProperty(ENVIRONMENT_PROPERTY))) {
        return "https://pdl-api.nais.adeo.no/graphql"
    } else {
        return "https://pdl-api.nais.preprod.local/graphql"
    }
}
