package no.nav.modiapersonoversikt.service.fpsak

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.fpsak.ForeldrepengerFpSak
import no.nav.modiapersonoversikt.consumer.fpsak.FpSakResponse
import no.nav.modiapersonoversikt.consumer.fpsak.FpSakService
import no.nav.modiapersonoversikt.consumer.fpsak.FpSakServiceImpl
import no.nav.modiapersonoversikt.consumer.fpsak.Utbetaling
import no.nav.modiapersonoversikt.consumer.fpsak.YtelseType
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.collections.List

internal class FpSakServiceTest {
    private val httpClient: OkHttpClient = mockk()
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private val fpSakService: FpSakService = FpSakServiceImpl("http://test-url", httpClient)

    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    val ytelseResponse =
        listOf(
            FpSakResponse(
                ytelse = YtelseType.FORELDREPENGER,
                saksnummer = "SAK123",
                utbetalinger =
                    listOf(
                        Utbetaling(fom = LocalDate.of(2024, 1, 1), tom = LocalDate.of(2024, 3, 31), BigDecimal(100)),
                        Utbetaling(fom = LocalDate.of(2024, 4, 1), tom = LocalDate.of(2024, 6, 30), BigDecimal(100)),
                    ),
            ),
        )

    @Test
    fun `skal hente ytelser for bruker`() {
        val mockResponse = mockk<Response>()
        val mockCall = mockk<Call>()

        every { httpClient.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        every { mockResponse.body?.string() } returns objectMapper.writeValueAsString(ytelseResponse)

        val method = FpSakServiceImpl::class.members.first { it.name == "hentYtelser" }
        val result = method.call(fpSakService, "12345678910", null, null)
        snapshot.assertMatches(result)
    }

    @Test
    fun `skal sortere utbetalinger og beregne fom og tom`() {
        val method = FpSakServiceImpl::class.members.first { it.name == "mapFpSakResponse" }
        val ytelseSortert = method.call(fpSakService, ytelseResponse) as List<ForeldrepengerFpSak>

        assertThat(ytelseSortert[0].fom).isEqualTo(LocalDate.of(2024, 1, 1))
        assertThat(ytelseSortert[0].tom).isEqualTo(LocalDate.of(2024, 6, 30))
        assertThat(ytelseSortert[0].perioder[0].fom).isEqualTo(LocalDate.of(2024, 4, 1))
    }
}
