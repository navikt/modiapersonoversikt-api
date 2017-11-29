package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CookieUtilTest {

    private String VALGT_ENHET = "4300";
    private String cookieNavn = CookieUtil.VALGT_ENHET_COOKIE_NAME_PREFIX + getSubjectHandler().getUid();;

    @BeforeAll
    static void before() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    void getSaksbehandlersValgteEnhet() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setCookies(new Cookie(cookieNavn, VALGT_ENHET));

        String valgtEnhet = CookieUtil.getSaksbehandlersValgteEnhet(httpRequest);

        assertEquals(VALGT_ENHET, valgtEnhet);
    }

}
