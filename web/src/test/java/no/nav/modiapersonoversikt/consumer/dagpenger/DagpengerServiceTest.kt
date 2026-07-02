package no.nav.modiapersonoversikt.consumer.dagpenger

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.apis.InterntApi
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.BeregnetDagDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.FagsystemDagpengerDto
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDate

internal class DagpengerServiceTest {
    private val apiClient: InterntApi = mockk()
    private val dagpengerService: DagpengerService = DagpengerServiceImpl(apiClient)

    val beregnetDagpengerResponse =
        listOf(
            BeregnetDagDagpengerDto(
                fraOgMed = LocalDate.of(2025, 1, 3),
                tilOgMed = LocalDate.of(2025, 1, 17),
                sats = 1000,
                utbetaltBeløp = 950,
                gjenståendeDager = 200,
                kilde = FagsystemDagpengerDto.decode("DP_SAK")!!,
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
        every { apiClient.dagpengerDatadelingV1BeregningerPost(any()) } returns beregnetDagpengerResponse
        val method = DagpengerServiceImpl::class.members.first { it.name == "hentDagpenger" }
        val result =
            method.call(
                dagpengerService,
                DatadelingRequestDagpengerDto("12345678910", LocalDate.of(2010, 2, 4)),
            )
        snapshot.assertMatches(result)
    }
}
