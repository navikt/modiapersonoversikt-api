package no.nav.modiapersonoversikt.consumer.dkif

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import no.nav.modiapersonoversikt.legacy.api.domain.dkif.generated.apis.DigitalKontaktinformasjonApi
import no.nav.modiapersonoversikt.legacy.api.domain.dkif.generated.apis.PingApi
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import okhttp3.OkHttpClient

class DkifServiceRestImpl(
    baseUrl: String = EnvironmentUtils.getRequiredProperty("DKIF_REST_URL")
) : Dkif.Service, Pingable {

    private val httpClient: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            LoggingInterceptor("Dkif") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    private val client = DigitalKontaktinformasjonApi(basePath = baseUrl, httpClient = httpClient)
    private val pingApi = PingApi(baseUrl, httpClient)

    override fun hentDigitalKontaktinformasjon(fnr: String): Dkif.DigitalKontaktinformasjon {
        val (feil, kontaktinfo) = client.digitalKontaktinformasjonUsingGET(
            authorization = AuthContextUtils.getToken()
                .map { "Bearer $it" }
                .orElseThrow { IllegalStateException("Fant ikke OIDC-token") },
            navCallId = getCallId(),
            navConsumerId = RestConstants.MODIABRUKERDIALOG_SYSTEM_USER,
            navPersonidenter = listOf(fnr),
            inkluderSikkerDigitalPost = true
        )

        val kontaktinfoTilBruker = kontaktinfo?.get(fnr)

        if (kontaktinfoTilBruker == null) {
            TjenestekallLogger.warn(
                header = "Feil ved henting fra dkif",
                fields = mapOf(
                    "fnr" to fnr,
                    "feil" to feil
                )
            )
            return Dkif.INGEN_KONTAKTINFO
        }

        return Dkif.DigitalKontaktinformasjon(
            personident = kontaktinfoTilBruker.personident,
            reservasjon = kontaktinfoTilBruker.reservert.toString(),
            epostadresse = Dkif.Epostadresse(value = kontaktinfoTilBruker.epostadresse),
            mobiltelefonnummer = Dkif.MobilTelefon(value = kontaktinfoTilBruker.mobiltelefonnummer)
        )
    }

    override fun ping(): SelfTestCheck? {
        return SelfTestCheck(
            "DkifRest",
            false,
            HealthCheck {
                try {
                    pingApi.getPingUsingGET(RestConstants.MODIABRUKERDIALOG_SYSTEM_USER)
                    HealthCheckResult.healthy()
                } catch (e: Exception) {
                    HealthCheckResult.unhealthy(e)
                }
            }
        )
    }
}
