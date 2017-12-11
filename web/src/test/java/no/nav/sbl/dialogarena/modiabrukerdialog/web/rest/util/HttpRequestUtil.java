package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

public class HttpRequestUtil {

    public static MockHttpServletRequest mockHttpServletRequestMedCookie(String ident, String valgtEnhet) {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setCookies(new Cookie("saksbehandlerinnstillinger-" + ident, valgtEnhet));
        return httpRequest;
    }

}
