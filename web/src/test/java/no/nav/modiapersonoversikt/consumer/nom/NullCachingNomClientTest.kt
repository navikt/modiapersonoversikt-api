package no.nav.modiapersonoversikt.consumer.nom

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.types.identer.NavIdent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class NullCachingNomClientTest {
    private val baseClient: NomClient = mockk()
    private val client = NullCachingNomClient(baseClient)
    private val navIdent1 = NavIdent("1")
    private val navIdent2 = NavIdent("2")
    private val navIdent3 = NavIdent("3")
    private val veiledere =
        listOf(
            VeilederNavn().setNavIdent(navIdent1),
            VeilederNavn().setNavIdent(navIdent3),
        )

    @Test
    internal fun `should not cache errors from underlying service`() {
        every { baseClient.finnNavn(any<NavIdent>()) } throws IllegalStateException("Something went wrong")

        repeat(2) {
            assertThatThrownBy { client.finnNavn(navIdent1) }
                .isExactlyInstanceOf(IllegalStateException::class.java)
                .hasMessage("Something went wrong")
        }

        verify(exactly = 2) { baseClient.finnNavn(any<NavIdent>()) }
    }

    @Test
    internal fun `should cache null value, but also throw error if no data is found`() {
        every { baseClient.finnNavn(any<NavIdent>()) } returns null

        assertThatThrownBy { client.finnNavn(navIdent1) }
            .isExactlyInstanceOf(IllegalStateException::class.java)
            .hasMessage("Fant ikke navn for NAV-ident: ${navIdent1.get()}")

        assertThatThrownBy { client.finnNavn(navIdent1) }
            .isExactlyInstanceOf(IllegalStateException::class.java)
            .hasMessage("Fant ikke navn for NAV-ident: ${navIdent1.get()}")

        verify(exactly = 1) { baseClient.finnNavn(any<NavIdent>()) }
    }

    @Test
    internal fun `should cache null values`() {
        every { baseClient.finnNavn(any<List<NavIdent>>()) } returns veiledere

        assertThat(client.finnNavn(listOf(navIdent1, navIdent2, navIdent3))).isEqualTo(veiledere)
        assertThat(client.finnNavn(listOf(navIdent1, navIdent2, navIdent3))).isEqualTo(veiledere)

        assertThat(client.finnNavn(navIdent1)).isEqualTo(veiledere.first())
        assertThat(client.finnNavn(navIdent3)).isEqualTo(veiledere.last())

        assertThatThrownBy { client.finnNavn(navIdent2) }
            .isExactlyInstanceOf(IllegalStateException::class.java)
            .hasMessage("Fant ikke navn for NAV-ident: ${navIdent2.get()}")
        assertThat(client.finnNavn(listOf(navIdent2))).isEmpty()

        verify(exactly = 1) { baseClient.finnNavn(any<List<NavIdent>>()) }
        verify(exactly = 0) { baseClient.finnNavn(any<NavIdent>()) }
    }

    @Test
    internal fun `should not refetch cached values`() {
        every { baseClient.finnNavn(navIdent1) } returns veiledere.first()
        every { baseClient.finnNavn(navIdent2) } returns null
        every { baseClient.finnNavn(any<List<NavIdent>>()) } answers {
            val identer = this.args[0] as List<NavIdent>
            veiledere.filter { identer.contains(it?.navIdent) }
        }

        client.finnNavn(navIdent1)
        assertThatThrownBy { client.finnNavn(navIdent2) }
            .isExactlyInstanceOf(IllegalStateException::class.java)
            .hasMessage("Fant ikke navn for NAV-ident: ${navIdent2.get()}")
        assertThat(client.finnNavn(listOf(navIdent1, navIdent2, navIdent3))).isEqualTo(veiledere)

        verify {
            baseClient.finnNavn(navIdent1)
            baseClient.finnNavn(navIdent2)
            baseClient.finnNavn(listOf(navIdent3))
        }
    }
}
