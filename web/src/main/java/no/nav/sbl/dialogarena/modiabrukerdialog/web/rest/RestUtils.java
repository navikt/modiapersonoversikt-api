package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.SaksbehandlerInnstillingerServiceImpl.saksbehandlerInnstillingerCookieId;

public class RestUtils {
    static String hentValgtEnhet(HttpServletRequest request) {
        String key = saksbehandlerInnstillingerCookieId();
        for (Cookie cookie : request.getCookies()) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException(String.format("Finner ikke cookie med key %s på session", key));
    }
}
