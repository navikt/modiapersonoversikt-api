package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import io.mockk.mockk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaverResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PatchOppgaverResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.time.LocalDate

open class RestOppgaveMockFactory {
    companion object {
        val ANSVARLIG_SAKSBEHANDLER = "Z554455"

        val mockOppgave: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "4100",
                oppgavetype = "SPM_OG_SVR",
                versjon = 1,
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                status = OppgaveJsonDTO.Status.AAPNET,
                aktivDato = LocalDate.now()
        )

        val mockOppgaveFerdigstiltUtenBeskrivelse: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "",
                temagruppe = Temagruppe.ANSOS.name,
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                endretAvEnhetsnr = "",
                status = OppgaveJsonDTO.Status.FERDIGSTILT,
                versjon = 1,
                tilordnetRessurs = "Z999998",
                opprettetAvEnhetsnr = "4100"
        )

        val mockOppgaveFerdigstiltMedBeskrivelse: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "",
                temagruppe = Temagruppe.ANSOS.name,
                tema = "KNA",
                behandlingstema = "ny beskrivelse",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                endretAvEnhetsnr = "",
                status = OppgaveJsonDTO.Status.FERDIGSTILT,
                versjon = 1,
                tilordnetRessurs = "Z999998",
                opprettetAvEnhetsnr = "4100"
        )

        val mockOppgaverFerdigstilt: PatchOppgaverResponseJsonDTO = PatchOppgaverResponseJsonDTO(
                suksess = 0,
                feilet = 1
        )

        val mockOppgaveResponse: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                temagruppe = "ARBD_KNA",
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                endretAvEnhetsnr = "",
                status = OppgaveJsonDTO.Status.AAPNET,
                versjon = 1,
                tilordnetRessurs = "Z999998",
                opprettetAvEnhetsnr = "4100"
        )

        val mockOpprettOppgaveResponse: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                temagruppe = "ARBD_KNA",
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                endretAvEnhetsnr = "",
                status = OppgaveJsonDTO.Status.AAPNET,
                versjon = 1,
                tilordnetRessurs = "",
                opprettetAvEnhetsnr = "4100"
        )

        val mockOpprettOppgaveResponseSkjermet: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                opprettetAvEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                temagruppe = "",
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                status = OppgaveJsonDTO.Status.AAPNET,
                versjon = 1,
                tildeltEnhetsnr = ""
        )

        val mockLeggTilbakeOppgaveResponse: OppgaveJsonDTO = OppgaveJsonDTO(
                id = 1234,
                opprettetAvEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "ny beskrivelse",
                temagruppe = Temagruppe.ANSOS.name,
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                aktivDato = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                status = OppgaveJsonDTO.Status.AAPNET,
                versjon = 1,
                tildeltEnhetsnr = "4100",
                endretAvEnhetsnr = "4110"
        )

        val mockOppgaverResponse: GetOppgaverResponseJsonDTO = GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 2,
                oppgaver = listOf(mockOppgaveResponse, mockOppgaveResponse.copy(id = 5678))
        )

        val mockAnsattEnhetListe = listOf(AnsattEnhet("4100", "NKS"))

        val tilgangskontrollContext: TilgangskontrollContext = mockk()
    }
}