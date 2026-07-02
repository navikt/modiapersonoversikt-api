package no.nav.modiapersonoversikt.consumer.dagpenger

import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.BeregnetDagDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.FagsystemDagpengerDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DagpengerVedtakTest {
    val initialDate = LocalDate.of(2025, 1, 1)
    val earliestDate = initialDate.plusDays(-14)
    val latestDate = initialDate.plusDays(14)
    val vedtak =
        Dagpenger(
            listOf(initialDate, latestDate, earliestDate).map {
                BeregnetDagDagpengerDto(
                    fraOgMed = it,
                    tilOgMed = it.plusDays(11),
                    sats = 1000,
                    utbetaltBeløp = 950,
                    gjenståendeDager = 200,
                    kilde = FagsystemDagpengerDto.decode("ARENA")!!,
                )
            },
        )

    @Test
    fun `eldsteFraOgMedDato returnerer første periode`() {
        assertThat(vedtak.eldsteFraOgMedDato).isEqualTo(earliestDate)
    }
}
