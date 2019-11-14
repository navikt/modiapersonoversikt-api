package no.nav.sbl.dialogarena.modiabrukerdialog.consumer;


import org.apache.wicket.util.cookies.CookieUtils;

import javax.servlet.http.Cookie;

public class PathlessCookieUtils extends CookieUtils {
    @Override
    protected void initializeCookie(Cookie cookie) {
        super.initializeCookie(cookie);
        cookie.setPath("/");
    }
}
