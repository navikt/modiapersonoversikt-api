package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.common.auth.subject.SubjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RestUtils {
    public static final Logger enhetCookieLogging = LoggerFactory.getLogger("EnhetCookieLogging");

    public static String saksbehandlerInnstillingerCookieId() {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        return "saksbehandlerinnstillinger-" + ident;
    }

    public static String saksbehandlerInnstillingerTimeoutCookieId() {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        return "saksbehandlerinnstillinger-timeout-" + ident;
    }

    public static String hentValgtEnhet(String enhet, HttpServletRequest request) {
        if (StringUtils.isNotBlank(enhet)) {
            return enhet;
        }
        String uri = request.getRequestURI();
        enhetCookieLogging.warn("[ENHETCOOKIE] Bruker enhet fra cookie, URI: {}", uri);
        String key = saksbehandlerInnstillingerCookieId();

        if (request.getCookies() == null) {
            throw new IllegalStateException("Ingen cookie er tilgjengelig på HTTP-requesten. Er du egentlig logget inn?");
        }
        for (Cookie cookie : request.getCookies()) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException(String.format("Finner ikke cookie med key %s på session", key));
    }
}

