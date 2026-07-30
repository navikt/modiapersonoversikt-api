package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.generated.HentOppfolgingStatus
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.generated.HentOppfolgingsEnhetOgVeileder
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
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
) : ArbeidsrettetOppfolging.Service {
    private val url = apiUrl.removeSuffix("/")

    @Cacheable
    override fun hentOppfolgingsinfo(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Info {
        val oppfolgingstatus = hentOppfolgingStatus(fodselsnummer)
        val enhetOgVeileder =
            when (oppfolgingstatus.underOppfolging) {
                true -> hentOppfolgingsEnhetOgVeileder(fodselsnummer)
                else -> null
            }
        return ArbeidsrettetOppfolging.Info(
            oppfolgingstatus.underOppfolging,
            oppfolgingstatus.erManuell,
            enhetOgVeileder?.veilederId?.let { ansattService.hentVeileder(NavIdent(it)) },
            enhetOgVeileder?.oppfolgingsenhet?.let {
                ArbeidsrettetOppfolging.OppfolgingsEnhet(
                    it.enhetId,
                    it.navn,
                )
            },
        )
    }

    @Cacheable
    override fun hentOppfolgingStatus(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Status {
        val data = runBlocking {
            graphQLClient.execute(
                HentOppfolgingStatus(HentOppfolgingStatus.Variables(fnr = fodselsnummer.get())),
                userTokenAuthorizationHeaders,
            )
                .assertNoErrors()
                .data
        } ?: error("Mangler data i HentOppfolgingStatus-respons")
        return ArbeidsrettetOppfolging.Status(
            underOppfolging = data.oppfolging?.erUnderOppfolging ?: false,
            erManuell = data.brukerStatus?.manuell?.erManuell ?: false,
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

    private fun hentOppfolgingsEnhetOgVeileder(fodselsnummer: Fnr): ArbeidsrettetOppfolging.EnhetOgVeileder {
        val result =
            runBlocking {
                graphQLClient.execute(
                    HentOppfolgingsEnhetOgVeileder(
                        HentOppfolgingsEnhetOgVeileder.Variables(fnr = fodselsnummer.get()),
                    ),
                    userTokenAuthorizationHeaders,
                )
            }
        val data = requireNotNull(result.data) { "Mangler data i HentOppfolgingsEnhetOgVeileder-respons" }
        return ArbeidsrettetOppfolging.EnhetOgVeileder(
            oppfolgingsenhet =
                data.oppfolgingsEnhet?.enhet?.let {
                    ArbeidsrettetOppfolging.OppfolgingsEnhet(
                        enhetId = it.id,
                        navn = it.navn,
                    )
                },
            veilederId = data.brukerStatus?.veilederTilordning?.veilederIdent,
        )
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val token = AuthContextUtils.requireBoundedClientOboToken(oboTokenClient)
        header("Authorization", "Bearer $token")
        header("Content-Type", "application/json")
    }
}
