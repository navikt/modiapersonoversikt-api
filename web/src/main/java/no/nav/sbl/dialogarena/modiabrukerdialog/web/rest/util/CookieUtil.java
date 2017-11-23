package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import no.nav.modig.core.context.SubjectHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CookieUtil {

    public static final String VALGT_ENHET_COOKIE_NAME_PREFIX = "saksbehandlerinnstillinger-";

    public static String getSaksbehandlersValgteEnhet(HttpServletRequest httpRequest) {
        if (httpRequest.getCookies() == null) {
            throw new IllegalStateException("Ingen cookie er tilgjengelig på HTTP-requesten. Er du egentlig logget inn?");
        }
        String innloggetSaksbehandlersIdent = SubjectHandler.getSubjectHandler().getUid();
        return Arrays.stream(httpRequest.getCookies())
                .filter(cookie -> cookie.getName().equals(VALGT_ENHET_COOKIE_NAME_PREFIX + innloggetSaksbehandlersIdent))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(IllegalStateException::new);
    }

}
