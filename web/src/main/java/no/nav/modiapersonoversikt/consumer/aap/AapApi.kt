package no.nav.modiapersonoversikt.consumer.aap

import no.nav.modiapersonoversikt.api.domain.aap.generated.apis.MaksimumApi
import no.nav.modiapersonoversikt.api.domain.aap.generated.models.ApiInternVedtakRequestApiInternDTO
import no.nav.modiapersonoversikt.api.domain.aap.generated.models.NonavaapapiinternVedtakUtenUtbetalingDTO
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.time.LocalDate

interface AapApi {
    fun hentArbeidsavklaringspengerSistePeriodePerVedtak(
        fnr: String,
        tilOgMedDato: String?,
        fraOgMedDato: String?,
    ): List<NonavaapapiinternVedtakUtenUtbetalingDTO>
}

@CacheConfig(cacheNames = ["tilgangsmaskinenCache"], keyGenerator = "userkeygenerator")
open class AapApiImpl(
    baseUrl: String,
    httpClient: OkHttpClient,
) : AapApi {
    val aapMaksimumApi = MaksimumApi(baseUrl, httpClient)

    fun hentArbeidsavklaringspengerPerUtbetalingsPeriode(
        fnr: String,
        tilOgMedDato: String?,
        fraOgMedDato: String?,
    ): List<NonavaapapiinternVedtakUtenUtbetalingDTO> {
        val fnrRequest =
            ApiInternVedtakRequestApiInternDTO(
                fnr,
                fraOgMedDato?.let { LocalDate.parse(it) },
                tilOgMedDato?.let { LocalDate.parse(it) },
            )
        return aapMaksimumApi
            .maksimumUtenUtbetalingPost(
                navCallid = getCallId(),
                apiInternVedtakRequestApiInternDTO = fnrRequest,
            )?.vedtak
            .orEmpty()
    }

    @Cacheable
    override fun hentArbeidsavklaringspengerSistePeriodePerVedtak(
        fnr: String,
        tilOgMedDato: String?,
        fraOgMedDato: String?,
    ): List<NonavaapapiinternVedtakUtenUtbetalingDTO> {
        val alleVedtakPerioder = hentArbeidsavklaringspengerPerUtbetalingsPeriode(fnr, tilOgMedDato, fraOgMedDato)
        return alleVedtakPerioder
            .groupBy { it.vedtakId }
            .map { vedtaksGruppe ->
                val periodeStart =
                    vedtaksGruppe.value
                        .minWithOrNull(compareBy { it.periode.fraOgMedDato })
                        ?.periode
                        ?.fraOgMedDato

                val sisteVedtak = vedtaksGruppe.value.maxWithOrNull(compareBy { it.periode.tilOgMedDato })

                sisteVedtak?.copy(
                    periode =
                        sisteVedtak.periode.copy(
                            fraOgMedDato = periodeStart,
                        ),
                )
            }.filterNotNull()
    }
}
