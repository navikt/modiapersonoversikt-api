package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.RestOppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import org.junit.jupiter.api.Test
import java.time.LocalDate.now

val mockOppgave: OppgaveJsonDTO = OppgaveJsonDTO(
        id = 1234,
        tildeltEnhetsnr = "4100",
        oppgavetype = "SPM_OG_SVR",
        versjon = 1,
        prioritet = OppgaveJsonDTO.Prioritet.NORM,
        status = OppgaveJsonDTO.Status.AAPNET,
        aktivDato = now()
)

val mockOppgaveFerdigstilt: OppgaveJsonDTO = OppgaveJsonDTO(
        id = 1234,
        tildeltEnhetsnr = "4100",
        oppgavetype = "SPM_OG_SVR",
        versjon = 1,
        prioritet = OppgaveJsonDTO.Prioritet.NORM,
        status = OppgaveJsonDTO.Status.FERDIGSTILT,
        aktivDato = mockOppgave.aktivDato
)

class RestOppgaveBehandlingServiceImplRedoTest {
    val apiClient: OppgaveApi = mockk()
    val kodeverksmapperService: KodeverksmapperService = mockk()
    val pdlOppslagService: PdlOppslagService = mockk()
    val tilgangskontroll: Tilgangskontroll = mockk()
    val ansattService: AnsattService = mockk()
    val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    val stsService: SystemUserTokenProvider = mockk()
    val oppgaveBehandlingService: RestOppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
            apiClient,
            kodeverksmapperService,
            pdlOppslagService,
            tilgangskontroll,
            ansattService,
            arbeidsfordelingService,
            stsService
    )

    @Test
    fun `skal hente og tilordne oppgave, setter 4100 som standard enhet`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns mockOppgave.asGetResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgave(
                    "1234",
                    Temagruppe.FMLI,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), any())
            apiClient.patchOppgave(any(), any(), PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4100",
                    tilordnetRessurs = "Z999998"
            ))
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, bruker saksbehandlers valgte enhet for ANSOS etc `() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns mockOppgave.asGetResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), any())
            apiClient.patchOppgave(any(), any(), PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    tilordnetRessurs = "Z999998"
            ))
        }
    }

    @Test
    fun `skal ferdigstille oppgave med beskrivelse`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns mockOppgave.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns mockOppgave.asPutResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns mockOppgaveFerdigstilt
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110",
                    "ny beskrivelse"
            )
        }

        verify {
            apiClient.hentOppgave(any(), any())
            apiClient.endreOppgave(any(), any(), any())
            apiClient.patchOppgave(any(), any(), PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT
            ))
        }
    }

    @Test
    fun `skal ferdigstille oppgaver`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns mockOppgave.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns mockOppgave.asPutResponse()
        every { apiClient.patchOppgaver(any(), any()) } returns mockOppgaveFerdigstilt.asPatchResponse()
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgaver(
                    listOf("1234"),
                    Temagruppe.ANSOS,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), any())
            apiClient.endreOppgave(any(), any(), any())
            apiClient.patchOppgaver(any(), PatchOppgaverRequestJsonDTO(
                    oppgaver = listOf(PatchJsonDTO(versjon = 1, id = 1234)),
                    status = PatchOppgaverRequestJsonDTO.Status.FERDIGSTILT,
                    endretAvEnhetsnr = "4110"
            ))
        }
    }

    @Test
    fun systemetLeggerTilbakeOppgaveUtenEndringer() {
    }

    @Test
    fun skalKonvertereFraOppgaveJsonDTOTilPutOppgaveResponseJsonDTO() {
    }

    @Test
    fun skalKonvertereFraGetOppgaveResponseJsonDTOTilOppgaveJsonDTO() {
    }

    @Test
    fun skalFinneTilordnaOppgave() {
    }

    @Test
    fun skalLeggeTilbakeTilordnetOppgaveUtenTilgang() {
    }
}
