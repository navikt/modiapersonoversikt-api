package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerCookieId;

public class CookieUtil {
    public static String getSaksbehandlersValgteEnhet(HttpServletRequest httpRequest) {
        if (httpRequest.getCookies() == null) {
            throw new IllegalStateException("Ingen cookie er tilgjengelig på HTTP-requesten. Er du egentlig logget inn?");
        }

        return Arrays.stream(httpRequest.getCookies())
                .filter(cookie -> cookie.getName().equals(saksbehandlerInnstillingerCookieId()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(IllegalStateException::new);
    }

}
