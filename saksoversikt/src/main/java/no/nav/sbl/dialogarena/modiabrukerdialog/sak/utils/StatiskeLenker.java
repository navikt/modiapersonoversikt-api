package no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public final class StatiskeLenker {

    public static final String TJENESTER_BASE = System.getProperty("tjenester.url");
    public static final String SOKNADINNSEING_URL = TJENESTER_BASE + "/soknadinnsending";
    public static final String DOKUMENINNSENDING_URL = TJENESTER_BASE + "/dokumentinnsending/oversikt";
    public static final String NAV_NO = System.getProperty("dialogarena.navnolink.url");
    public static final String NAV_NO_ETTERSENDING = NAV_NO + "/no/Person/Skjemaer-for-privatpersoner/Ettersendelse";

}
