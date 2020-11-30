package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1ServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock.Companion.get
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired

open class LeggTilbakeOppgaveDelegateTest @Autowired constructor(
        private var restOppgaveServiceMock: OppgaveApi,
        private var ansattServiceMock: AnsattService,
        private var restOppgaveBehandlingService: RestOppgaveBehandlingServiceImpl,
        private var tildelOppgaveMock: TildelOppgaveV1,
        var arbeidsfordelingMock: ArbeidsfordelingV1Service,
        private var tilgangskontroll: Tilgangskontroll,
        val kodeverksmapperService: KodeverksmapperService,
        val pdlOppslagService: PdlOppslagService
){
    val VALGT_ENHET : String = "4300"
    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val apiClient = OppgaveApi(OPPGAVE_BASEURL)

    @BeforeEach
    open fun before() {
        mockTjenester()
        restOppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(kodeverksmapperService, pdlOppslagService, tilgangskontroll, ansattServiceMock, arbeidsfordelingMock)
    }

    private fun mockTjenester() {
        restOppgaveServiceMock = mock(OppgaveApi::class.java)
        ansattServiceMock = mockAnsattService()
        tildelOppgaveMock = mock(TildelOppgaveV1::class.java)
        arbeidsfordelingMock = mock(ArbeidsfordelingV1ServiceImpl::class.java)
        tilgangskontroll = get()
    }

    private fun mockAnsattService(): AnsattService {
        val ansattServiceMock = mock(AnsattService::class.java)
        `when`(ansattServiceMock.hentAnsattNavn(ArgumentMatchers.anyString())).thenReturn(RestOppgaveMockFactory.ANSVARLIG_SAKSBEHANDLER)
        return ansattServiceMock
    }


}