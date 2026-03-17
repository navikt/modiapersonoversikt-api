package no.nav.modiapersonoversikt.consumer.dagpenger

import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.FagsystemDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.YtelseTypeDagpengerDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class PseudoDagpengerVedtakTest {
    val initialDate = LocalDate.of(2025, 1, 1)
    val earliestDate = initialDate.plusDays(-14)
    val latestDate = initialDate.plusDays(14)
    val vedtak =
        PseudoDagpengerVedtak(
            listOf(initialDate, latestDate, earliestDate).map {
                PeriodeDagpengerDto(
                    fraOgMedDato = it,
                    tilOgMedDato = it.plusDays(11),
                    kilde = FagsystemDagpengerDto.decode("ARENA")!!,
                    ytelseType = YtelseTypeDagpengerDto.decode("DAGPENGER_PERMITTERING_FISKEINDUSTRI")!!,
                )
            },
        )

    @Test
    fun `nyesteFraOgMedDato returnerer nyeste periode`() {
        assertThat(vedtak.nyesteFraOgMedDato).isEqualTo(latestDate)
    }
}
