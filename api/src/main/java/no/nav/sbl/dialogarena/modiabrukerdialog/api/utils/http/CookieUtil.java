package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerCookieId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerTimeoutCookieId;

public class CookieUtil {

    public static String getSaksbehandlersValgteEnhet(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new IllegalStateException("Ingen cookie er tilgjengelig på HTTP-requesten. Er du egentlig logget inn?");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(RestUtils.saksbehandlerInnstillingerCookieId()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(IllegalStateException::new);
    }

    public static void setSaksbehandlersValgteEnhet(HttpServletResponse response, String enhetId) {
        response.addCookie(createCookie(saksbehandlerInnstillingerCookieId(), enhetId, "/modiabrukerdialog/"));
        // response.addCookie(createCookie(saksbehandlerInnstillingerCookieId(), enhetId, "/modiapersonoversikt/"));

        response.addCookie(createCookie(saksbehandlerInnstillingerTimeoutCookieId(), "", "/modiabrukerdialog/"));
        // response.addCookie(createCookie(saksbehandlerInnstillingerTimeoutCookieId(), "", "/modiapersonoversikt/"));
    }

    private static Cookie createCookie(String name, String value, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(3600 * 24 * 365);
        cookie.setPath(path);
        return cookie;
    }
}
