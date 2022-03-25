package no.nav.modiapersonoversikt.rest;

import no.nav.modiapersonoversikt.service.saker.Sak;
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils;
import no.nav.modiapersonoversikt.legacy.api.utils.http.AuthContextTestUtils;
import no.nav.modiapersonoversikt.rest.journalforing.JournalforingApi;
import no.nav.modiapersonoversikt.rest.journalforing.JournalforingController;
import no.nav.modiapersonoversikt.service.saker.EnhetIkkeSatt;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;

import static no.nav.modiapersonoversikt.rest.journalforing.JournalforingController.FEILMELDING_UTEN_ENHET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class JournalforingControllerTest {

    private static final String SAKSBEHANDLERS_IDENT = "z999643";

    @Test
    @DisplayName("Knytter til sak og returnerer 200 OK")
    void journalforingKnytterTilSak() {

        JournalforingController journalforingController = new JournalforingController(mock(JournalforingApi.class), TilgangskontrollMock.get());

        ResponseEntity<Void> response = AuthContextTestUtils.withIdent(SAKSBEHANDLERS_IDENT, () ->
                journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(), null, mockHttpRequest())
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Forespørsler som feiler kaster 500 Internal Server Error")
    void journalforingSomFeilerKasterFeil() {
        JournalforingApi mock = mock(JournalforingApi.class);
        doThrow(RuntimeException.class).when(mock).knyttTilSak(any(), any(), any(), any());
        JournalforingController journalforingController = new JournalforingController(mock, TilgangskontrollMock.get());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                AuthContextTestUtils.withIdent(SAKSBEHANDLERS_IDENT, () ->
                        journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(), null, mockHttpRequest())
                )
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    @DisplayName("Forespørsler uten enhet kaster 500 Internal Server Error med message satt i body")
    void journalforingUtenEnhetKasterFeil() {
        JournalforingApi mock = mock(JournalforingApi.class);
        doThrow(EnhetIkkeSatt.class).when(mock).knyttTilSak(any(), any(), any(), any());
        JournalforingController journalforingController = new JournalforingController(mock, TilgangskontrollMock.get());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                AuthContextTestUtils.withIdent(SAKSBEHANDLERS_IDENT, () ->
                        journalforingController.knyttTilSak("10108000398", "traad-id", new Sak(), null, mockHttpRequest())
                )
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals(FEILMELDING_UTEN_ENHET, exception.getReason());
    }

    private MockHttpServletRequest mockHttpRequest() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setCookies(new Cookie(RestUtils.saksbehandlerInnstillingerCookieId(), "1337"));
        return mockHttpServletRequest;
    }

}
