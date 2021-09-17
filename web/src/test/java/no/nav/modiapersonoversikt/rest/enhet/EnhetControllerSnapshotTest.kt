package no.nav.modiapersonoversikt.rest.enhet

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.*
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService
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
    lateinit var organisasjonEnhetKontaktinformasjonService: OrganisasjonEnhetKontaktinformasjonService

    @MockkBean
    lateinit var organisasjonEnhetV2Service: OrganisasjonEnhetV2Service

    @MockkBean
    lateinit var arbeidsfordeling: ArbeidsfordelingV1Service

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
        every { organisasjonEnhetV2Service.finnNAVKontor(any(), any()) } returns Optional.of(
            AnsattEnhet("1234", "NAV Test")
        )

        getJson("/rest/enheter?gt=010101")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                snapshot.assertMatches(it.response.contentAsString)
            }
    }

    private fun gittKontaktinformasjon() {
        every { organisasjonEnhetKontaktinformasjonService.hentKontaktinformasjon(any()) } returns OrganisasjonEnhetKontaktinformasjon()
            .withEnhetId("1234")
            .withEnhetNavn("NAV Test")
            .withKontaktinformasjon(
                Kontaktinformasjon().withPublikumsmottakliste(
                    listOf(
                        Publikumsmottak()
                            .withApningstider(
                                Apningstider().withApningstid(
                                    listOf(
                                        Apningstid()
                                            .withApentFra(Klokkeslett(8, 0, 0))
                                            .withApentTil(Klokkeslett(8, 0, 0))
                                            .withUkedag(Ukedag.FREDAG)
                                    )
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
