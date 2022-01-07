package no.nav.modiapersonoversikt.rest.enhet

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
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
import java.util.*

@WebMvcTest(EnhetController::class)
@ExtendWith(SnapshotExtension::class)
internal class EnhetControllerSnapshotTest(val snapshot: SnapshotExtension) {
    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock.get()
    }

    @MockkBean
    lateinit var norgapi: NorgApi

    @MockkBean
    lateinit var arbeidsfordeling: ArbeidsfordelingService

    @MockkBean
    lateinit var ansattService: AnsattService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    internal fun `hent enhetsdata gitt enhetid`() {
        gittKontaktinformasjon()
        getJson("/rest/enheter/1234")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                snapshot.assertMatches(it.response.contentAsString)
            }
    }

    @Test
    internal fun `finn enhet gitt gt og diskresjonskode`() {
        gittKontaktinformasjon()
        every { norgapi.finnNavKontor(any(), any()) } returns NorgDomain.Enhet(
            "1234",
            "NAV Test",
            NorgDomain.EnhetStatus.AKTIV
        )

        getJson("/rest/enheter?gt=010101")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                snapshot.assertMatches(it.response.contentAsString)
            }
    }

    private fun gittKontaktinformasjon() {
        every { norgapi.hentKontaktinfo(any()) } returns NorgDomain.EnhetKontaktinformasjon(
            enhetId = "1234",
            enhetNavn = "NAV Test",
            publikumsmottak = listOf(
                NorgDomain.Publikumsmottak(
                    besoksadresse = null,
                    apningstider = listOf(
                        NorgDomain.Apningstid(
                            NorgDomain.Ukedag.FREDAG,
                            apentFra = "08:00",
                            apentTil = "08:00",
                        )
                    )
                )
            )
        )
    }

    private fun getJson(url: String): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
    }
}
