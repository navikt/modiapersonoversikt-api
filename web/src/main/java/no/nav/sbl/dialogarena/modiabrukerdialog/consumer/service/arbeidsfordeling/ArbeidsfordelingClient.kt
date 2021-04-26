package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

import com.fasterxml.jackson.core.type.TypeReference
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.json.JsonMapper
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.EnhetsGeografiskeTilknytning
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants.*
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

val log: Logger = LoggerFactory.getLogger(ArbeidsfordelingClient::class.java)
private val norgClient = RestClient.baseClient()
private val objectMapper = JsonMapper.defaultObjectMapper()

private val AnsattEnhetResponse = object : TypeReference<List<ArbeidsfordelingEnhet>>() {}
private val EnhetsGeografiskTilknytningResponse = object : TypeReference<List<EnhetsGeografiskeTilknytning>>() {}

open class ArbeidsfordelingClient {
    companion object {
        private val NORG2_URL = when {
            "p" == EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY) -> "https://app.adeo.no/norg2"
            "q1" == EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY) -> "https://app-q1.adeo.no/norg2"
            else -> "https://app-q0.adeo.no/norg2"
        }
    }

    @Autowired
    private lateinit var stsService: SystemUserTokenProvider

    open fun hentGTForEnhet(enhet: String): List<EnhetsGeografiskeTilknytning> {
        return norgClient
            .newCall(
                Request.Builder()
                    .url("$NORG2_URL/api/v1/enhet/navkontorer/$enhet")
                    .build()
            )
            .execute()
            .body()!!
            .string()
            .let { objectMapper.readValue(it, EnhetsGeografiskTilknytningResponse) }
    }

    open fun hentArbeidsfordeling(behandling: Optional<Behandling>, geografiskTilknytning: GeografiskTilknytning, oppgavetype: String, fagomrade: String, erEgenAnsatt: Boolean): List<AnsattEnhet> {
        val veilederOidcToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }
        val consumerOidcToken: String = stsService.systemUserToken
        val arbeidskritereieFordelingSkjermet = ArbeidskritereieFordelingSkjermet(
            behandlingstema = behandling?.map(Behandling::getBehandlingstema).orElse(null),
            behandlingstype = behandling?.map(Behandling::getBehandlingstype).orElse(null),
            geografiskOmraade = geografiskTilknytning.value?.let { it }.toString(),
            oppgavetype = oppgavetype,
            diskresjonskode = geografiskTilknytning?.diskresjonskode ?: null,
            tema = fagomrade,
            enhetsnummer = null,
            temagruppe = null,
            skjermet = erEgenAnsatt
        )

        val response = norgClient
            .newCall(
                Request.Builder()
                    .url("$NORG2_URL/api/v1/arbeidsfordeling/enheter/bestmatch")
                    .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                    .post(
                        RequestBody.create(
                            MediaType.parse("application/json"),
                            objectMapper.writeValueAsString(arbeidskritereieFordelingSkjermet)
                        )
                    )
                    .build()
            )
            .execute()

        if (response.code() != 200) {
            log.error("Kunne ikke hente enheter fra arbeidsfordeling. ResponseStatus: ${response.code()}")
            return emptyList()
        } else {
            return response
                .body()!!
                .string()
                .let {
                    objectMapper.readValue(it, AnsattEnhetResponse)
                }
                .map { enhet ->
                    AnsattEnhet(enhet.enhetNr, enhet.navn, "AKTIV")
                }
        }
    }
}
