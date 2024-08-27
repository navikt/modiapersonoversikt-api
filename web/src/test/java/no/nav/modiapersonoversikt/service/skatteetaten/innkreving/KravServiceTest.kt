package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
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
        every { skatteetatenInnkrevingClient.getKravdetaljer(any(), any()) } returns kravdetaljer

        val result = kravService.hentKrav(KravId("kravId"))

        assertThat(result).isEqualTo(kravdetaljer)
    }

    @Test
    fun `hent kravdetaljer for en krav-id returnerer null hvis det ikke finnes noen kravdetaljer`() {
        every { skatteetatenInnkrevingClient.getKravdetaljer(any(), any()) } returns null

        val result = kravService.hentKrav(KravId("kravId"))

        assertThat(result).isNull()
    }
}
