package no.nav.modiapersonoversikt.legacy.sak.utils;


import no.nav.common.utils.EnvironmentUtils;

public final class StatiskeLenker {

    public static final String TJENESTER_BASE = EnvironmentUtils.getRequiredProperty("TJENESTER_URL");
    public static final String SOKNADINNSEING_URL = TJENESTER_BASE + "/soknadinnsending";
    public static final String DOKUMENINNSENDING_URL = TJENESTER_BASE + "/dokumentinnsending/oversikt";
    public static final String NAV_NO = EnvironmentUtils.getRequiredProperty("dialogarena.navnolink.url");
    public static final String NAV_NO_ETTERSENDING = NAV_NO + "/no/Person/Skjemaer-for-privatpersoner/Ettersendelse";

}
