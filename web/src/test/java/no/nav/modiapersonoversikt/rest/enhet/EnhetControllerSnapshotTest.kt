package no.nav.modiapersonoversikt.rest.enhet

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

private val norgapiMock = mockk<NorgApi>()
private val arbeidsfordelingMock = mockk<ArbeidsfordelingService>()
private val ansattServiceMock = mockk<AnsattService>()

@WebMvcTest(EnhetController::class)
@ExtendWith(SnapshotExtension::class)
internal class EnhetControllerSnapshotTest(val snapshot: SnapshotExtension) {
    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock.get()

        @Bean
        open fun norgapi(): NorgApi = norgapiMock

        @Bean
        open fun arbeidsfordeling() = arbeidsfordelingMock

        @Bean
        open fun ansattService() = ansattServiceMock
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    internal fun `hent ansatte gitt enhetid`() {
        every { ansattServiceMock.ansatteForEnhet(any()) } returns listOf(
            Ansatt(
                "fornavn",
                "etternavn",
                "Z999999"
            )
        )

        getJson("/rest/enheter/1234/ansatte")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                snapshot.assertMatches(it.response.contentAsString)
            }
    }

    @Test
    internal fun `hent alle enheter`() {
        every { norgapiMock.hentEnheter(any(), any(), any()) } returns listOf(
            NorgDomain.Enhet(
                "1234",
                "NAV Test",
                NorgDomain.EnhetStatus.AKTIV,
                oppgavebehandler = false
            )
        )
        getJson("/rest/enheter/oppgavebehandlere/alle")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                snapshot.assertMatches(it.response.contentAsString)
            }
    }

    private fun getJson(url: String): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
    }
}
