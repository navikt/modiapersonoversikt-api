package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KravServiceTest {
    private val skatteetatenInnkrevingClient = mockk<SkatteetatenInnkrevingClient>()
    private val kravService = KravService(skatteetatenInnkrevingClient)

    private val kravdetaljer =
        Kravdetaljer(
            kravgrunnlag = Kravgrunnlag(datoNaarKravVarBesluttetHosOppdragsgiver = null),
            krav =
                listOf(
                    Krav(
                        kravType = "kravType",
                        opprinneligBeløp = 200.0,
                        gjenståendeBeløp = 100.0,
                    ),
                ),
        )

    @Test
    fun `hent kravdetaljer for en krav-id returnerer kravdetaljer`() {
        every { skatteetatenInnkrevingClient.hentKravdetaljer(any()) } returns kravdetaljer

        val result = kravService.hentKravdetaljer(KravdetaljerId("kravId"))

        assertThat(result).isEqualTo(kravdetaljer)
    }

    @Test
    fun `hent kravdetaljer for en krav-id returnerer null hvis det ikke finnes noen kravdetaljer`() {
        every { skatteetatenInnkrevingClient.hentKravdetaljer(any()) } returns null

        val result = kravService.hentKravdetaljer(KravdetaljerId("kravId"))

        assertThat(result).isNull()
    }

    @Test
    fun `hent alle krav for en fnr returnerer liste med alle krav`() {
        val alleKravdetaljer = listOf(kravdetaljer)
        every { skatteetatenInnkrevingClient.hentAlleKravdetaljer(any()) } returns alleKravdetaljer

        val result = kravService.hentAlleKravdetaljer(Fnr("12345678910"))

        assertThat(result).isEqualTo(alleKravdetaljer)
    }
}
