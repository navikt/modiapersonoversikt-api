package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak.EnhetIkkeSatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.Feilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.TilgangskontrollMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.Response;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.JournalforingController.FEILMELDING_UTEN_ENHET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class JournalforingControllerTest {

    private static final String SAKSBEHANDLERS_IDENT = "z999643";

    @BeforeAll
    static void beforeAll() {
//        SubjectHandlerUtil.medSaksbehandler(SAKSBEHANDLERS_IDENT);
    }

    @Test
    @DisplayName("Knytter til sak og returnerer 200 OK")
    void journalforingKnytterTilSak() throws JournalforingFeilet {
        JournalforingController journalforingController = new JournalforingController(mock(SakerService.class), TilgangskontrollMock.get());

        Response response = journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(),
                mockHttpRequest());

        assertEquals(Response.Status.OK, response.getStatusInfo());
    }

    @Test
    @DisplayName("Forespørsler som feiler kaster 500 Internal Server Error")
    void journalforingSomFeilerKasterFeil() throws JournalforingFeilet {
        SakerService mock = mock(SakerService.class);
        doThrow(RuntimeException.class).when(mock).knyttBehandlingskjedeTilSak(any(), any(), any(), any());
        JournalforingController journalforingController = new JournalforingController(mock, TilgangskontrollMock.get());

        Response response = journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(),
                mockHttpRequest());

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
    }

    @Test
    @DisplayName("Forespørsler uten enhet kaster 500 Internal Server Error med message satt i body")
    void journalforingUtenEnhetKasterFeil() throws JournalforingFeilet {
        SakerService mock = mock(SakerService.class);
        doThrow(EnhetIkkeSatt.class).when(mock).knyttBehandlingskjedeTilSak(any(), any(), any(), any());
        JournalforingController journalforingController = new JournalforingController(mock, TilgangskontrollMock.get());

        Response response = journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(),
                mockHttpRequest());

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        assertEquals(FEILMELDING_UTEN_ENHET, ((Feilmelding) response.getEntity()).getMessage());
    }

    private MockHttpServletRequest mockHttpRequest() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(RestUtils.saksbehandlerInnstillingerCookieId(), "1337"));
        return mockHttpServletRequest;
    }

}