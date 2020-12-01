package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.RestOppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import org.junit.jupiter.api.BeforeAll
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

class RestOppgaveBehandlingServiceImplRedoTest {
    val apiClient: OppgaveApi = mockk()
    val stsService: SystemUserTokenProvider = mockk()
    val oppgaveBehandlingService: RestOppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
            apiClient,
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
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
    fun skalFerdigstilleOppgaver() {
        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
            )
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
