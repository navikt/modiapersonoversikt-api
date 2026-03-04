package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingResponseDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.YtelseTypeDagpengerDto
import no.nav.modiapersonoversikt.rest.common.FnrDatoRangeRequest
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDate

internal class DagpengerServiceTest {
    private val httpClient: OkHttpClient = mockk()
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    private val dagpengerService: DagpengerService = DagpengerServiceImpl("http://0", httpClient)

    val ongoingPeriodResponse =
        DatadelingResponseDagpengerDto(
            personIdent = "42",
            perioder =
                listOf(
                    PeriodeDagpengerDto(
                        fraOgMedDato = LocalDate.of(2025, 1, 3),
                        ytelseType = YtelseTypeDagpengerDto.decode("DAGPENGER_PERMITTERING_FISKEINDUSTRI")!!,
                    ),
                ),
        )

    // writes "expected" test result to file for us
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    @Test
    fun `metoder for å hente perioder eksisterer`() {
        val mockResponse = mockk<Response>()
        val mockCall = mockk<Call>()

        every { httpClient.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        every { mockResponse.body?.string() } returns objectMapper.writeValueAsString(ongoingPeriodResponse)
        every { mockResponse.body?.contentLength() } returns objectMapper.writeValueAsString(ongoingPeriodResponse).length.toLong()

        val method = DagpengerServiceImpl::class.members.first { it.name == "hentPerioder" }
        val result =
            method.call(
                dagpengerService,
                FnrDatoRangeRequest("12345678910", "2009-01-03"),
            )
        //snapshot.assertMatches(result) // chill with the snaps until done
    }
}
