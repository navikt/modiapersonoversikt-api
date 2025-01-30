package no.nav.modiapersonoversikt.service.oppfolgingsinfo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.VeilarbvedtaksstotteServiceImpl
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Siste14AVedtakV2Api
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.HovedmalDetaljert
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.InnsatsgruppeDetaljert
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.KodeverkDTO
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.Siste14aVedtakDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class VeilarbvedtaksstotteServiceTest {
    private val siste14AVedtakV2Api: Siste14AVedtakV2Api = mockk()
    private val kodeverkFor14AVedtakApi: KodeverkFor14AVedtakApi = mockk()
    private val fnr = Fnr("12345678910")

    @BeforeEach
    fun setupStandardMocker() {
        every { siste14AVedtakV2Api.hentSiste14aVedtak(any()) } answers {
            Siste14aVedtakDTO(
                innsatsgruppe = Siste14aVedtakDTO.Innsatsgruppe.STANDARD_INNSATS,
                hovedmal = Siste14aVedtakDTO.Hovedmal.SKAFFE_ARBEID,
                fattetDato = OffsetDateTime.now(),
                fraArena = false,
            )
        }
        every { kodeverkFor14AVedtakApi.getKodeverk() } answers {
            KodeverkDTO(
                innsatsgrupper =
                    listOf(
                        InnsatsgruppeDetaljert(kode = "STANDARD_INNSATS", beskrivelse = "Standard innsats"),
                    ),
                hovedmal = listOf(HovedmalDetaljert(kode = "SKAFFE_ARBEID", beskrivelse = "Skaffe arbeide")),
            )
        }
    }

    @Test
    fun `hent Siste 14a Vedtak`() {
        val veilarbvedtaksstotteService =
            VeilarbvedtaksstotteServiceImpl(siste14AVedtakV2Api, kodeverkFor14AVedtakApi)
        val siste14aVedtak = veilarbvedtaksstotteService.hentSiste14aVedtak(fnr)

        verifySequence {
            kodeverkFor14AVedtakApi.getKodeverk()
            siste14AVedtakV2Api.hentSiste14aVedtak(any())
        }

        assertThat(siste14aVedtak?.innsatsgruppe?.kode).isEqualTo("STANDARD_INNSATS")
        assertThat(siste14aVedtak?.hovedmal?.kode).isEqualTo("SKAFFE_ARBEID")
    }
}
