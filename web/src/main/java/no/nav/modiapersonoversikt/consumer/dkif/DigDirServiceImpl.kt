import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.rest.client.RestClient
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.digdir.generated.apis.PersonControllerApi
import no.nav.modiapersonoversikt.consumer.digdir.generated.apis.PingControllerApi
import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.infrastructure.TjenestekallLogger
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import okhttp3.OkHttpClient

class DigDirServiceImpl(
    baseUrl: String = EnvironmentUtils.getRequiredProperty("DIG_DIR_REST_URL"),
    machineToMachineTokenClient: MachineToMachineTokenClient,
    private val cache: Cache<Fnr, Dkif.DigitalKontaktinformasjon> = CacheUtils.createCache()
) : Dkif.Service {

    private val scope = DownstreamApi.parse(EnvironmentUtils.getRequiredProperty("DIG_DIR_SCOPE"))

    private val httpClient: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(
            LoggingInterceptor("digdir-krr-proxy") { request ->
                requireNotNull(request.header("Nav-Call-Id"))
            }
        )
        .addInterceptor(
            AuthorizationInterceptor {
                machineToMachineTokenClient.createMachineToMachineToken(scope)
            }
        )
        .build()

    private val client = PersonControllerApi(basePath = baseUrl, httpClient = httpClient)
    private val pingApi = PingControllerApi(baseUrl, httpClient)

    override fun hentDigitalKontaktinformasjon(fnr: String): Dkif.DigitalKontaktinformasjon {
        return requireNotNull(
            cache.get(Fnr(fnr)) {
                client.runCatching {
                    getPerson(
                        navPersonident = fnr,
                        navCallId = getCallId(),
                        inkluderSikkerDigitalPost = true
                    )
                }.map { data ->
                    Dkif.DigitalKontaktinformasjon(
                        personident = data.personident,
                        reservasjon = data.reservert?.toString(),
                        epostadresse = Dkif.Epostadresse(
                            value = data.epostadresse,
                            sistOppdatert = data.epostadresseOppdatert?.toLocalDate(),
                            sistVerifisert = data.epostadresseVerifisert?.toLocalDate()
                        ),
                        mobiltelefonnummer = Dkif.MobilTelefon(
                            value = data.mobiltelefonnummer,
                            sistOppdatert = data.mobiltelefonnummerOppdatert?.toLocalDate(),
                            sistVerifisert = data.mobiltelefonnummerVerifisert?.toLocalDate()
                        )
                    )
                }.getOrElse {
                    TjenestekallLogger.warn(
                        header = "Feil ved henting av digital kontaktinformasjon fra digdir",
                        fields = mapOf(
                            "fnr" to fnr,
                            "exception" to it,
                        )
                    )
                    Dkif.INGEN_KONTAKTINFO
                }
            }
        )
    }

    override fun ping(): SelfTestCheck {
        return SelfTestCheck(
            "DigDirRest",
            false
        ) {
            try {
                pingApi.getPing()
                HealthCheckResult.healthy()
            } catch (e: Exception) {
                HealthCheckResult.unhealthy(e)
            }
        }
    }
}
