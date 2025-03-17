package no.nav.modiapersonoversikt.service.oppfolgingsinfo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.VeilarbvedtaksstotteServiceImpl
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Gjeldende14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class VeilarbvedtaksstotteServiceTest {
    private val gjeldende14AVedtakApi: Gjeldende14AVedtakApi = mockk()
    private val kodeverkFor14AVedtakApi: KodeverkFor14AVedtakApi = mockk()
    private val fnr = Fnr("12345678910")

    @BeforeEach
    fun setupStandardMocker() {
        every { gjeldende14AVedtakApi.hentGjeldende14aVedtakEksternt(any()) } answers {
            Gjeldende14aVedtakDto(
                innsatsgruppe = Gjeldende14aVedtakDto.Innsatsgruppe.GODE_MULIGHETER,
                hovedmal = Gjeldende14aVedtakDto.Hovedmal.SKAFFE_ARBEID,
                fattetDato = OffsetDateTime.now(),
            )
        }
        every { kodeverkFor14AVedtakApi.getInnsatsgruppeOgHovedmalKodeverk() } answers {
            KodeverkV2DTO(
                innsatsgrupper =
                    listOf(
                        InnsatsgruppeKodeverkV2DTO(
                            kode = InnsatsgruppeKodeverkV2DTO.Kode.GODE_MULIGHETER,
                            beskrivelse = "GODE MULIGHETER",
                            gammelKode = InnsatsgruppeKodeverkV2DTO.GammelKode.STANDARD_INNSATS,
                            arenaKode = InnsatsgruppeKodeverkV2DTO.ArenaKode.BATT,
                        ),
                    ),
                hovedmal =
                    listOf(
                        HovedmalKodeverkV2DTO(
                            kode = HovedmalKodeverkV2DTO.Kode.SKAFFE_ARBEID,
                            beskrivelse = "Skaffe arbeide",
                        ),
                    ),
            )
        }
    }

    @Test
    fun `hent gjeldende 14a Vedtak`() {
        val veilarbvedtaksstotteService =
            VeilarbvedtaksstotteServiceImpl(gjeldende14AVedtakApi, kodeverkFor14AVedtakApi)
        val gjeldende14aVedtak = veilarbvedtaksstotteService.hentGjeldende14aVedtak(fnr)

        verifySequence {
            kodeverkFor14AVedtakApi.getInnsatsgruppeOgHovedmalKodeverk()
            gjeldende14AVedtakApi.hentGjeldende14aVedtakEksternt(any())
        }

        assertThat(gjeldende14aVedtak?.innsatsgruppe?.kode).isEqualTo("GODE_MULIGHETER")
        assertThat(gjeldende14aVedtak?.hovedmal?.kode).isEqualTo("SKAFFE_ARBEID")
    }
}
