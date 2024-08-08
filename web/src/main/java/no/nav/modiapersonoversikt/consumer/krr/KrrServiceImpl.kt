import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.krr.generated.apis.PersonControllerApi
import no.nav.modiapersonoversikt.consumer.krr.generated.apis.PingControllerApi
import no.nav.modiapersonoversikt.consumer.krr.generated.models.DigitalKontaktinformasjonDTO
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.personoversikt.common.logging.TjenestekallLogg
import okhttp3.OkHttpClient
import java.time.ZoneId
import java.time.ZonedDateTime

class KrrServiceImpl(
    baseUrl: String = EnvironmentUtils.getRequiredProperty("KRR_REST_URL"),
    httpClient: OkHttpClient,
    private val cache: Cache<Fnr, Krr.DigitalKontaktinformasjon> = CacheUtils.createCache(),
) : Krr.Service {
    private val client = PersonControllerApi(basePath = baseUrl, httpClient = httpClient)
    private val pingApi = PingControllerApi(baseUrl, httpClient)

    override fun hentDigitalKontaktinformasjon(fnr: String): Krr.DigitalKontaktinformasjon {
        return requireNotNull(
            cache.get(Fnr(fnr)) {
                client.runCatching {
                    getPerson(
                        navPersonident = fnr,
                        navCallId = getCallId(),
                        inkluderSikkerDigitalPost = true,
                    )
                }.map { data ->
                    if (data != null) {
                        mapToDigitalKontaktInformasjon(data)
                    } else {
                        TjenestekallLogg.warn(
                            header = "Feil ved henting av digital kontaktinformasjon fra krr",
                            fields =
                                mapOf(
                                    "fnr" to fnr,
                                    "exception" to it,
                                ),
                        )
                        Krr.INGEN_KONTAKTINFO
                    }
                }.getOrElse {
                    TjenestekallLogg.warn(
                        header = "Feil ved henting av digital kontaktinformasjon fra krr",
                        fields =
                            mapOf(
                                "fnr" to fnr,
                                "exception" to it,
                            ),
                    )
                    Krr.INGEN_KONTAKTINFO
                }
            },
        )
    }

    override fun ping(): SelfTestCheck {
        return SelfTestCheck(
            "KrrRest",
            false,
        ) {
            try {
                pingApi.getPing()
                HealthCheckResult.healthy()
            } catch (e: Exception) {
                HealthCheckResult.unhealthy(e)
            }
        }
    }

    private fun mapToDigitalKontaktInformasjon(dto: DigitalKontaktinformasjonDTO) =
        Krr.DigitalKontaktinformasjon(
            personident = dto.personident,
            reservasjon = dto.reservert?.toString(),
            epostadresse =
                dto.epostadresse?.let { epostadresse ->
                    Krr.Epostadresse(
                        value = epostadresse,
                        sistOppdatert = toLocalDate(dto.epostadresseOppdatert),
                        sistVerifisert = toLocalDate(dto.epostadresseVerifisert),
                    )
                },
            mobiltelefonnummer =
                dto.mobiltelefonnummer?.let { mobiltelefonnummer ->
                    Krr.MobilTelefon(
                        value = mobiltelefonnummer,
                        sistOppdatert = toLocalDate(dto.mobiltelefonnummerOppdatert),
                        sistVerifisert = toLocalDate(dto.mobiltelefonnummerVerifisert),
                    )
                },
        )

    private fun toLocalDate(value: ZonedDateTime?) = value?.withZoneSameInstant(ZoneId.systemDefault())?.toLocalDate()
}
