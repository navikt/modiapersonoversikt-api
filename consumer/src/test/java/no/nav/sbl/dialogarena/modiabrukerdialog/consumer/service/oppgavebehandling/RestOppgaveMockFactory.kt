package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import java.time.LocalDate

internal object RestOppgaveMockFactory {
    const val ANSVARLIG_SAKSBEHANDLER = "z554455"
    const val OPPGAVE_ID = "123123123"
    const val FNR_MOSS_TESTFAMILIEN = "07063000250"

    fun mockHentOppgaveResponseMedTilordning() : OppgaveJsonDTO{
        return lagOppgave().copy(beskrivelse = "opprinnelig beskrivelse")
    }

    fun lagOppgave(): OppgaveJsonDTO {
        return OppgaveJsonDTO(
                id = OPPGAVE_ID.toLong(),
                tilordnetRessurs = ANSVARLIG_SAKSBEHANDLER,
                aktoerId = FNR_MOSS_TESTFAMILIEN,
                tildeltEnhetsnr = "ansvarligenhetid",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                temagruppe = "ARBD_KNA",
                tema = "ARBEID_HJE",
                oppgavetype = "oppgavetype",
                aktivDato = LocalDate.now(),
                prioritet = OppgaveJsonDTO.Prioritet.NORM,
                status = OppgaveJsonDTO.Status.OPPRETTET,
                versjon = 1,
                saksreferanse = "saksnummer"
        )
    }
}