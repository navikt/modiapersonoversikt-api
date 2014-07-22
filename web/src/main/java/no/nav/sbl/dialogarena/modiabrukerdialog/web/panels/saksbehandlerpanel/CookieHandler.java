package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import org.apache.wicket.util.cookies.CookieDefaults;
import org.apache.wicket.util.cookies.CookieUtils;

public class CookieHandler {

    private static CookieUtils cookieUtils;
    static {
        CookieDefaults cookieDefaults = new CookieDefaults();
        cookieDefaults.setMaxAge(12 * 60 * 60);
        cookieUtils = new CookieUtils(cookieDefaults);
    }

    public static CookieUtils getCookieUtils() {
        return cookieUtils;
    }
}
