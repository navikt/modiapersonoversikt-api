package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import kotlinx.datetime.toKotlinLocalDate
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.infrastructure.ClientException
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.infrastructure.ServerException
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.KravdetaljerResponse
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.Kravgrunnlag
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Grunnlag
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Innkrevingskrav
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravClient
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravId
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.slf4j.LoggerFactory

class SkatteetatenHttpInnkrevingskravClient(
    private val kravdetaljerApi: KravdetaljerApi,
    private val clientId: String,
    private val unleashService: UnleashService,
) : InnkrevingskravClient,
    Pingable {
    private val log = LoggerFactory.getLogger(SkatteetatenHttpInnkrevingskravClient::class.java)

    override fun hentInnkrevingskrav(innkrevingskravId: InnkrevingskravId): Innkrevingskrav? =
        runCatching {
            kravdetaljerApi
                .getKravdetaljer(
                    klientid = clientId,
                    accept = "application/json",
                    kravidentifikator = innkrevingskravId.value,
                    kravidentifikatortype = KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR.name,
                )?.toDomain()
        }.getOrElse {
            when (it) {
                is UnsupportedOperationException, is ClientException, is ServerException -> {
                    log.error("Feil ved henting av kravdetaljer", it)
                    null
                }

                else -> throw it
            }
        }

    // Lager en liste med opptil 10 mockede kravdetaljer
    override fun hentAlleInnkrevingskrav(fnr: Fnr): List<Innkrevingskrav> = listOf()

    override fun ping(): SelfTestCheck =
        SelfTestCheck("SkatteetatenInnkrevingHttpClient", false) {
            if (!unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API)) {
                return@SelfTestCheck HealthCheckResult.unhealthy("Feature toggled off")
            }

            // Midlertidig ping som kun fungerer i test.
            val kravdetaljer =
                hentInnkrevingskrav(
                    InnkrevingskravId("87b5a5c6-17ea-413a-ad80-b6c3406188fa"),
                )

            if (kravdetaljer == null) {
                HealthCheckResult.unhealthy("Feil ved henting av kravdetaljer")
            } else {
                HealthCheckResult.healthy()
            }
        }
}

private fun KravdetaljerResponse.toDomain(): Innkrevingskrav = Innkrevingskrav(kravgrunnlag.toDomain(), kravlinjer ?: emptyList())

private fun Kravgrunnlag.toDomain(): Grunnlag =
    Grunnlag(
        datoNaarKravVarBesluttetHosOppdragsgiver = datoNaarKravVarBesluttetHosOppdragsgiver?.toKotlinLocalDate(),
    )
