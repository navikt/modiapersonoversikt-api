package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.GsonBuilder
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*

import no.nav.sbl.rest.RestUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import javax.inject.Inject
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity.json
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


val log = LoggerFactory.getLogger(ArbeidsfordelingClient::class.java)



fun <T> Client.with(fn: (Client) -> T): T {
    return try {
        fn(this)
    } finally {
        this.close()
    }
}

fun createClient() : Client = RestUtils.createClient()
        .run {
            register(KotlinModule())
            this
        }

open class ArbeidsfordelingClient {
    private val OPPSLAG_URL = getEnvironmentUrl()
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @Inject
    private lateinit var stsService: StsServiceImpl


    open fun hentArbeidsfordeling(behandling: Optional<Behandling>, geografiskTilknytning: GeografiskTilknytning, oppgavetype: String, fagomrade: String, erEgenAnsatt: Boolean): List<AnsattEnhet> {
        val veilederOidcToken : String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }
        val consumerOidcToken : String = stsService.hentConsumerOidcToken()
        val arbeidskritereieFordelingSkjermet: ArbeidskritereieFordelingSkjermet = ArbeidskritereieFordelingSkjermet(
                behandlingstema = behandling?.map(Behandling::getBehandlingstema).orElse(null),
                behandlingstype = behandling?.map(Behandling::getBehandlingstype).orElse(null),
                geografiskOmraade = geografiskTilknytning.value.toString(),
                oppgavetype =oppgavetype,
                diskresjonskode = geografiskTilknytning?.diskresjonskode ?: null,
                tema = fagomrade,
                enhetsnummer = null,
                temagruppe = null,
                skjermet = erEgenAnsatt
        )

        return createClient().with { client ->
            val response = client.target(OPPSLAG_URL)
                    .request()
                    .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                    .post(json(arbeidskritereieFordelingSkjermet))

                if (response.status != 200) {
                log.error("Kunne ikke hente enheter fra arbeidsfordeling. ResponseStatus: ${response.status}")

                emptyList()
            } else {
                class AnsattEnhetResponse : GenericType<List<ArbeidsfordelingEnhet>>()
                    response
                        .readEntity(AnsattEnhetResponse())
                        .map { enhet ->
                            AnsattEnhet(enhet.enhetNr, enhet.navn, "AKTIV")
                        }
            }
        }
    }

    private fun getEnvironmentUrl(): String {
        return if ("p".equals(System.getProperty(ENVIRONMENT_PROPERTY))) {
            "https://app.adeo.no/norg2/api/v1/arbeidsfordeling/enheter/bestmatch"
        } else {
            "https://app-q0.adeo.no/norg2/api/v1/arbeidsfordeling/enheter/bestmatch"
        }
    }
}
