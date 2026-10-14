package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.consumer.pensjon.PensjonSak
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

internal class YtelseControllerTest {
    private val saker =
        listOf(
            sak(1, "2023-01-01", "2023-12-31"),
            sak(2, "2024-02-01", null),
            sak(3, "2025-01-01", null),
            sak(4, null, "2024-02-01"),
            sak(5, null, null),
        )

    @Test
    fun `returnerer alle saker når fom og tom mangler`() {
        val resultat = saker.filtrerPensjonSaker(fom = null, tom = null)

        assertEquals(saker, resultat)
    }

    @Test
    fun `filtrerer saker fra fom når tom mangler`() {
        val resultat = saker.filtrerPensjonSaker(fom = "2024-02-01", tom = null)

        assertEquals(listOf(2L, 3L, 4L, 5L), resultat.map { it.sakid })
    }

    @Test
    fun `filtrerer saker til tom når fom mangler`() {
        val resultat = saker.filtrerPensjonSaker(fom = null, tom = "2024-02-01")

        assertEquals(listOf(1L, 2L, 4L, 5L), resultat.map { it.sakid })
    }

    @Test
    fun `filtrerer saker som overlapper datointervallet inkludert grensene`() {
        val saker =
            listOf(
                sak(1, "2023-12-01", "2023-12-31"),
                sak(2, "2023-12-31", "2024-01-01"),
                sak(3, "2023-12-15", "2024-01-15"),
                sak(4, "2024-01-15", "2024-01-30"),
                sak(5, "2024-01-16", "2024-01-30"),
            )

        val resultat = saker.filtrerPensjonSaker(fom = "2024-01-01", tom = "2024-01-15")

        assertEquals(listOf(2L, 3L, 4L), resultat.map { it.sakid })
    }

    private fun sak(
        sakid: Long,
        fomDato: String?,
        tomDato: String?,
    ) = PensjonSak(
        sakid = sakid,
        sakType = "ALDER",
        sakStatus = "LOPENDE",
        fomDato = fomDato?.let(LocalDate::parse),
        tomDato = tomDato?.let(LocalDate::parse),
        enhetsId = "1234",
    )
}
