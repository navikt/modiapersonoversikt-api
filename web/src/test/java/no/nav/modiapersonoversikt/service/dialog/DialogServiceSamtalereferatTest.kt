package no.nav.modiapersonoversikt.service.dialog

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingFraDTO
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.sfhenvendelse.grupperSamtalereferater
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class DialogServiceSamtalereferatTest {
    @Test
    fun `frontend far samme trad for samlet v1 og grupperte v2 referater`() {
        val tidspunkt = OffsetDateTime.parse("2024-01-01T12:00:00Z")
        val fra = MeldingFraDTO(identType = MeldingFraDTO.IdentType.SYSTEM)
        val head =
            HenvendelseDTO(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                fnr = "00000000000",
                aktorId = "syntetisk-aktor",
                opprettetDato = tidspunkt,
                feilsendt = false,
                kjedeId = "",
                gjeldendeTemagruppe = "ARBD",
                meldinger = listOf(MeldingDTO(meldingsId = "head", sendtDato = tidspunkt, fra = fra, fritekst = "Første melding")),
            )
        val neste =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                meldinger =
                    listOf(
                        MeldingDTO(meldingsId = "andre", sendtDato = tidspunkt.plusMinutes(1), fra = fra, fritekst = "Andre melding"),
                    ),
            )
        val kodeverk = mockk<EnhetligKodeverk.Service>()
        every { kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA) } returns EnhetligKodeverk.Kodeverk("tema", emptyMap())
        val ansatt = mockk<AnsattService>()
        every { ansatt.hentVeiledere(any()) } returns emptyMap()
        val sf = mockk<SfHenvendelseService>()
        val dialog = DialogServiceImpl(sf, mockk<OppgaveBehandlingService>(), ansatt, kodeverk)
        val v1 =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                meldinger = head.meldinger.orEmpty() + neste.meldinger.orEmpty(),
            )

        every { sf.hentHenvendelser(any<EksternBruker>(), any()) } returns listOf(v1)
        val forventet = dialog.hentMeldinger(head.fnr, "0101")
        every { sf.hentHenvendelser(any<EksternBruker>(), any()) } returns grupperSamtalereferater(listOf(head, neste))

        assertThat(dialog.hentMeldinger(head.fnr, "0101")).isEqualTo(forventet)
        assertThat(forventet.single().traadId).isEqualTo("head")
        assertThat(forventet.single().temagruppe).isEqualTo("FMLI")
        assertThat(forventet.single().meldinger.map { it.temagruppe }).containsExactly("FMLI", "FMLI")
        assertThat(forventet.single().meldinger).hasSize(2)
    }
}
