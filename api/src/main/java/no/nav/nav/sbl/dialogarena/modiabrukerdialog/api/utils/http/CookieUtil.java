package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerCookieId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerTimeoutCookieId;

public class CookieUtil {

    public static String getSaksbehandlersValgteEnhet(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new IllegalStateException("Ingen cookie er tilgjengelig på HTTP-requesten. Er du egentlig logget inn?");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(saksbehandlerInnstillingerCookieId()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(IllegalStateException::new);
    }

    public static void setSaksbehandlersValgteEnhet(HttpServletResponse response, String enhetId) {
        Cookie enhetCookie = new Cookie(saksbehandlerInnstillingerCookieId(), enhetId);
        enhetCookie.setMaxAge(3600 * 24 * 365);
        enhetCookie.setPath("/modiabrukerdialog/");

        Cookie timeoutCookie = new Cookie(saksbehandlerInnstillingerTimeoutCookieId(), "");
        timeoutCookie.setMaxAge(3600 * 12);
        timeoutCookie.setPath("/modiabrukerdialog/");

        response.addCookie(enhetCookie);
        response.addCookie(timeoutCookie);
    }
}
