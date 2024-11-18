package no.nav.modiapersonoversikt.service.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.Kravlinje
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InnkrevingskravServiceTest {
    private val innkrevingskravClient = mockk<InnkrevingskravClient>()
    private val unleashService = mockk<UnleashService>()
    private val innkrevingskravService = InnkrevingskravService(innkrevingskravClient, unleashService)

    private val innkrevingskrav =
        Innkrevingskrav(
            grunnlag = Grunnlag(datoNaarKravVarBesluttetHosOppdragsgiver = null),
            krav =
                listOf(
                    Kravlinje(
                        kravlinjetype = "kravType",
                        opprinneligBeloep = 200.0,
                        gjenstaaendeBeloep = 100.0,
                    ),
                ),
        )

    @Test
    fun `hent kravdetaljer for en krav-id returnerer kravdetaljer`() {
        every { unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API_MOCK.propertyKey) } returns false
        every { innkrevingskravClient.hentInnkrevingskrav(any()) } returns innkrevingskrav

        val result = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId("kravId"))

        assertThat(result?.kravLinjer?.size).isEqualTo(1)
    }

    @Test
    fun `hent kravdetaljer for en krav-id returnerer null hvis det ikke finnes noen kravdetaljer`() {
        every { unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API_MOCK.propertyKey) } returns false
        every { innkrevingskravClient.hentInnkrevingskrav(any()) } returns null

        val result = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId("kravId"))

        assertThat(result).isNull()
    }

    @Test
    fun `hent alle krav for en fnr returnerer liste med alle krav`() {
        every { unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API_MOCK.propertyKey) } returns false
        val alleKravdetaljer = listOf(innkrevingskrav)
        every { innkrevingskravClient.hentAlleInnkrevingskrav(any()) } returns alleKravdetaljer

        val result = innkrevingskravService.hentAlleInnkrevingskrav(Fnr("12345678910"))

        assertThat(result.size).isEqualTo(1)
        assertThat(result.first().kravLinjer?.size).isEqualTo(1)
    }
}
