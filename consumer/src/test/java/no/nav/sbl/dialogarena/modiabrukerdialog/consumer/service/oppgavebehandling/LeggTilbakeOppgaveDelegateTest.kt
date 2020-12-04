package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.Test
import java.time.LocalDate


open class LeggTilbakeOppgaveDelegateTest {
    val apiClient: OppgaveApi = mockk()
    val kodeverksmapperService: KodeverksmapperService = mockk()
    val pdlOppslagService: PdlOppslagService = mockk()
    val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(RestOppgaveMockFactory.tilgangskontrollContext)
    val ansattService: AnsattService = mockk()
    val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    val stsService: SystemUserTokenProvider = mockk()
    val leggTilbakeOppgaveDelegate: LeggTilbakeOppgaveDelegate = LeggTilbakeOppgaveDelegate(
            RestOppgaveBehandlingServiceImpl(
                    apiClient,
                    kodeverksmapperService,
                    pdlOppslagService,
                    tilgangskontroll,
                    ansattService,
                    arbeidsfordelingService,
                    stsService
            ),
            arbeidsfordelingService
    )

    @Test
    fun `skal legge tilbake oppgave`() {
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()
        every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns RestOppgaveMockFactory.mockAnsattEnhetListe
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            leggTilbakeOppgaveDelegate.leggTilbake(
                    RestOppgaveMockFactory.mockOppgaveResponse,
                    LeggTilbakeOppgaveRequest(
                            "4110",
                            "1234",
                            "ny beskrivelse",
                            Temagruppe.ANSOS
                    )
            )
        }

        verify { apiClient.endreOppgave(any(), any(), PutOppgaveRequestJsonDTO(
                id = 1234,
                tildeltEnhetsnr = "4100",
                aktoerId = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = String.format("--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110") + "ny beskrivelse" + "\n\n" + "beskrivelse",
                temagruppe = Temagruppe.ANSOS.name,
                tema = "KNA",
                behandlingstema = "",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                endretAvEnhetsnr = "4110",
                status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                versjon = 1,
                tilordnetRessurs = ""
        )) }
    }

}
