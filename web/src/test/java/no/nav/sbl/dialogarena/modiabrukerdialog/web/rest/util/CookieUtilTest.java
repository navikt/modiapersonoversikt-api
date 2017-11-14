package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieUtilTest {

    private static final String SAKSBEHANDLERS_IDENT = "z999666";
    private String VALGT_ENHET = "4300";

    @Test
    void getSaksbehandlersValgteEnhet() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setCookies(new Cookie(CookieUtil.VALGT_ENHET_COOKIE_NAME_PREFIX + SAKSBEHANDLERS_IDENT, VALGT_ENHET));

        String valgtEnhet = CookieUtil.getSaksbehandlersValgteEnhet(httpRequest);

        assertEquals(VALGT_ENHET, valgtEnhet);
    }

}