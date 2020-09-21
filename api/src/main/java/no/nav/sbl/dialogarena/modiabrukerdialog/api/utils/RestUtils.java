package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.common.auth.subject.SubjectHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RestUtils {

    public static String saksbehandlerInnstillingerCookieId() {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        return "saksbehandlerinnstillinger-" + ident;
    }

    public static String saksbehandlerInnstillingerTimeoutCookieId() {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        return "saksbehandlerinnstillinger-timeout-" + ident;
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

