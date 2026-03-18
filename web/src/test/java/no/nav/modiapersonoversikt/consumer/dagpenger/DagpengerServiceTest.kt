package no.nav.modiapersonoversikt.consumer.dagpenger

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.apis.InterntApi
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingResponseDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.FagsystemDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.YtelseTypeDagpengerDto
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDate

internal class DagpengerServiceTest {
    private val apiClient: InterntApi = mockk()
    private val dagpengerService: DagpengerService = DagpengerServiceImpl(apiClient)

    val ongoingPeriodResponse =
        DatadelingResponseDagpengerDto(
            personIdent = "42",
            perioder =
                listOf(
                    PeriodeDagpengerDto(
                        fraOgMedDato = LocalDate.of(2025, 1, 3),
                        kilde = FagsystemDagpengerDto.decode("DP_SAK")!!,
                        ytelseType = YtelseTypeDagpengerDto.decode("DAGPENGER_PERMITTERING_FISKEINDUSTRI")!!,
                    ),
                ),
        )

    // writes expected test result to file for us
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    /**
     * Sanity check for the shape of input and output
     */
    @Test
    fun `metode for å hente perioder er velutformet`() {
        val mockResponse = mockk<DatadelingResponseDagpengerDto>()
        every { apiClient.dagpengerDatadelingV1PerioderPost(any()) } returns ongoingPeriodResponse
        val method = DagpengerServiceImpl::class.members.first { it.name == "hentVedtak" }
        val result =
            method.call(
                dagpengerService,
                DatadelingRequestDagpengerDto("12345678910", LocalDate.of(2010, 2, 4)),
            )
        snapshot.assertMatches(result) // chill with the snaps until done
    }
}
