package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling

import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.EnhetsGeografiskeTilknytning
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnvironmentStrategy.ENVIRONMENT_PROPERTY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import javax.inject.Inject
import javax.ws.rs.client.Entity.json
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


val log = LoggerFactory.getLogger(ArbeidsfordelingClient::class.java)
private val norgClient = RestUtils
        .createClient()

private val AnsattEnhetResponse = object : GenericType<List<ArbeidsfordelingEnhet>>() {}
private val EnhetsGeografiskTilknytningResponse = object : GenericType<List<EnhetsGeografiskeTilknytning>>() {}

open class ArbeidsfordelingClient {
    companion object {
        private val NORG2_URL = if ("p".equals(EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY))) {
            "https://app.adeo.no/norg2"
        } else if ("q1".equals(EnvironmentUtils.getRequiredProperty(ENVIRONMENT_PROPERTY))) {
            "https://app-q1.adeo.no/norg2"
        } else {
            "https://app-q0.adeo.no/norg2"
        }
    }

    @Inject
    private lateinit var stsService: SystemUserTokenProvider

    open fun hentGTForEnhet(enhet: String): List<EnhetsGeografiskeTilknytning> {
        return norgClient
                .target(NORG2_URL)
                .path("/api/v1/enhet/navkontorer/$enhet")
                .request()
                .get(EnhetsGeografiskTilknytningResponse)
    }

    open fun hentArbeidsfordeling(behandling: Optional<Behandling>, geografiskTilknytning: GeografiskTilknytning, oppgavetype: String, fagomrade: String, erEgenAnsatt: Boolean): List<AnsattEnhet> {
        val veilederOidcToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }
        val consumerOidcToken: String = stsService.systemUserAccessToken
        val arbeidskritereieFordelingSkjermet: ArbeidskritereieFordelingSkjermet = ArbeidskritereieFordelingSkjermet(
                behandlingstema = behandling?.map(Behandling::getBehandlingstema).orElse(null),
                behandlingstype = behandling?.map(Behandling::getBehandlingstype).orElse(null),
                geografiskOmraade = geografiskTilknytning.value?.let { it }.toString(),
                oppgavetype = oppgavetype,
                diskresjonskode = geografiskTilknytning?.diskresjonskode?: null,
                tema = fagomrade,
                enhetsnummer = null,
                temagruppe = null,
                skjermet = erEgenAnsatt
        )

        val response = norgClient
                .target(NORG2_URL)
                .path("/api/v1/arbeidsfordeling/enheter/bestmatch")
                .request()
                .header(NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                .header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                .post(json(arbeidskritereieFordelingSkjermet))

        if (response.status != 200) {
            log.error("Kunne ikke hente enheter fra arbeidsfordeling. ResponseStatus: ${response.status}")
            return emptyList()
        } else {
            class AnsattEnhetResponse : GenericType<List<ArbeidsfordelingEnhet>>()
            return response
                    .readEntity(AnsattEnhetResponse())
                    .map { enhet ->
                        AnsattEnhet(enhet.enhetNr, enhet.navn, "AKTIV")
                    }
        }
    }
}
