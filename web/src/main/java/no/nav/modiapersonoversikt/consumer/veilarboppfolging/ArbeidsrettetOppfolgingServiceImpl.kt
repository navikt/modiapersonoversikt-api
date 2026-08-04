package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.generated.HentOppfolgingsinfo
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.http.assertNoErrors
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import okhttp3.Request
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

@CacheConfig(cacheNames = ["oppfolgingsinfoCache"], keyGenerator = "userkeygenerator")
open class ArbeidsrettetOppfolgingServiceImpl(
    apiUrl: String,
    private val ansattService: AnsattService,
    private val graphQLClient: GraphQLKtorClient,
    private val oboTokenClient: BoundedOnBehalfOfTokenClient,
    private val consumerId: String = AppConstants.APP_NAME,
) : ArbeidsrettetOppfolging.Service {
    private val url = apiUrl.removeSuffix("/")

    @Cacheable
    override fun hentOppfolgingsinfo(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Info {
        val data =
            runBlocking {
                graphQLClient
                    .execute(
                        HentOppfolgingsinfo(HentOppfolgingsinfo.Variables(fnr = fodselsnummer.get())),
                        userTokenAuthorizationHeaders,
                    ).assertNoErrors()
                    .data
            } ?: return ArbeidsrettetOppfolging.Info(
                erUnderOppfolging = false,
                erManuell = false,
                veileder = null,
                oppfolgingsenhet = null,
            )
        return ArbeidsrettetOppfolging.Info(
            erUnderOppfolging = data.oppfolging?.erUnderOppfolging ?: false,
            erManuell = data.brukerStatus?.manuell?.erManuell ?: false,
            veileder =
                data.brukerStatus
                    ?.veilederTilordning
                    ?.veilederIdent
                    ?.let { ansattService.hentVeileder(NavIdent(it)) },
            oppfolgingsenhet =
                data.oppfolgingsEnhet?.enhet?.let {
                    ArbeidsrettetOppfolging.OppfolgingsEnhet(
                        enhetId = it.id,
                        navn = it.navn,
                    )
                },
        )
    }

    override fun ping() {
        val request =
            Request
                .Builder()
                .url("$url/ping")
                .build()

        RestClient
            .baseClient()
            .newCall(request)
            .execute()
            .body
            ?.string()
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val token = AuthContextUtils.requireBoundedClientOboToken(oboTokenClient)
        header("Authorization", "Bearer $token")
        header("Content-Type", "application/json")
        header(RestConstants.NAV_CONSUMER_ID_HEADER, consumerId)
    }
}
