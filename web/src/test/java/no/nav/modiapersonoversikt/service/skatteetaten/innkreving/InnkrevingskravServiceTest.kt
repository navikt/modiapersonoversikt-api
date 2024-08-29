package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InnkrevingskravServiceTest {
    private val innkrevingskravClient = mockk<InnkrevingskravClient>()
    private val innkrevingskravService = InnkrevingskravService(innkrevingskravClient)

    private val innkrevingskrav =
        Innkrevingskrav(
            grunnlag = Grunnlag(datoNaarKravVarBesluttetHosOppdragsgiver = null),
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
        every { innkrevingskravClient.hentInnkrevingskrav(any()) } returns innkrevingskrav

        val result = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId("kravId"))

        assertThat(result).isEqualTo(innkrevingskrav)
    }

    @Test
    fun `hent kravdetaljer for en krav-id returnerer null hvis det ikke finnes noen kravdetaljer`() {
        every { innkrevingskravClient.hentInnkrevingskrav(any()) } returns null

        val result = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId("kravId"))

        assertThat(result).isNull()
    }

    @Test
    fun `hent alle krav for en fnr returnerer liste med alle krav`() {
        val alleKravdetaljer = listOf(innkrevingskrav)
        every { innkrevingskravClient.hentAlleInnkrevingskrav(any()) } returns alleKravdetaljer

        val result = innkrevingskravService.hentAlleInnkrevingskrav(Fnr("12345678910"))

        assertThat(result).isEqualTo(alleKravdetaljer)
    }
}
