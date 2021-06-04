package no.nav.modiapersonoversikt.api.utils.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static no.nav.modiapersonoversikt.api.utils.RestUtils.*;

public class CookieUtil {
    public static void setSaksbehandlersValgteEnhet(HttpServletResponse response, String enhetId) {
        enhetCookieLogging.warn("[ENHETCOOKIE] Satt valgt enhet cookie");

        response.addCookie(createCookie(saksbehandlerInnstillingerCookieId(), enhetId, "/"));
        response.addCookie(createCookie(saksbehandlerInnstillingerTimeoutCookieId(), "", "/"));
    }

    private static Cookie createCookie(String name, String value, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(3600 * 24 * 365);
        cookie.setPath(path);
        return cookie;
    }
}
