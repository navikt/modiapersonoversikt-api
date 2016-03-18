package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class RestUtils {

    public static String saksbehandlerInnstillingerCookieId() {
        return "saksbehandlerinnstillinger-" + getSubjectHandler().getUid();
    }

    public static String hentValgtEnhet(HttpServletRequest request) {
        String key = saksbehandlerInnstillingerCookieId();
        for (Cookie cookie : request.getCookies()) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException(String.format("Finner ikke cookie med key %s på session", key));
    }
}

