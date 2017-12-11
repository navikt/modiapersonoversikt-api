package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieUtilTest {

    private static final String SAKSBEHANDLERS_IDENT = "z999666";
    private String VALGT_ENHET = "4300";

    @BeforeAll
    static void before() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    void getSaksbehandlersValgteEnhet() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setCookies(new Cookie("saksbehandlerinnstillinger-" + SAKSBEHANDLERS_IDENT, VALGT_ENHET));

        String valgtEnhet = CookieUtil.getSaksbehandlersValgteEnhet(httpRequest);

        assertEquals(VALGT_ENHET, valgtEnhet);
    }

}
