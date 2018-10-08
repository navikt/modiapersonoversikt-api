package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saker

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.DokumentMetadataService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SakstemaService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.JournalV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import javax.ws.rs.NotAuthorizedException
import kotlin.test.assertFailsWith

internal class SakerControllerTest {
    private val saksoversiktService: SaksoversiktService = mock()
    private val sakstemaService: SakstemaService = mock()
    private val saksService: SaksService = mock()
    private val tilgangskontrollService: TilgangskontrollService = mock()
    private val innsyn: JournalV2Service = mock()
    private val dokumentMetadataService: DokumentMetadataService = mock()
    private val unleashService: UnleashService = mock()

    private val sakerController = SakerController(saksoversiktService, sakstemaService, saksService,
            tilgangskontrollService, innsyn, dokumentMetadataService, unleashService)

    @BeforeEach
    fun before() {
        whenever(unleashService.isEnabled(Feature.NYTT_VISITTKORT)).thenReturn(true)
        whenever(tilgangskontrollService.markerIkkeJournalforte(any())).then {  }
        whenever(saksoversiktService.fjernGamleDokumenter(any())).then {  }
        whenever(tilgangskontrollService.harEnhetTilgangTilTema(any(), any())).thenReturn(true)
    }

    @Test
    fun `Tester at man ikke har tilgang ved manipulert cookie`() {
        whenever(tilgangskontrollService.harGodkjentEnhet(any())).thenReturn(false)
        val request = MockHttpServletRequest()

        assertFailsWith<NotAuthorizedException> { sakerController.hentSakstema(request, "") }
    }
}