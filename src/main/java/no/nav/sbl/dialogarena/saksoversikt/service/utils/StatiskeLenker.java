package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import static java.lang.System.getProperty;

public final class StatiskeLenker {

    public static final String TJENESTER_BASE = getProperty("tjenester.url");
    public static final String SOKNADINNSEING_URL = TJENESTER_BASE + "/soknadinnsending";
    public static final String DOKUMENINNSENDING_URL = TJENESTER_BASE + "/dokumentinnsending/oversikt";
    public static final String NAV_NO = getProperty("dialogarena.navnolink.url");
    public static final String NAV_NO_ETTERSENDING = NAV_NO + "/no/Person/Skjemaer-for-privatpersoner/Ettersendelse";

}
