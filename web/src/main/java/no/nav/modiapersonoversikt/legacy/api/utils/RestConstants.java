package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.common.utils.EnvironmentUtils;

public class RestConstants {
    public static final String AUTHORIZATION = "Authorization";
    public static final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    public static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    public static final String NAV_CONSUMER_ID_HEADER = "Nav-Consumer-Id";
    public static final String TEMA_HEADER = "Tema";
    public static final String ALLE_TEMA_HEADERVERDI = "GEN";

    public static final String AUTH_SEPERATOR = " ";
    public static final String AUTH_METHOD_BEARER = "Bearer";

    public static final String SECURITY_TOKEN_SERVICE_DISCOVERYURL = EnvironmentUtils.getRequiredProperty("SECURITY_TOKEN_SERVICE_DISCOVERY_URL");

    public static final String MODIABRUKERDIALOG_SYSTEM_USER = "srvModiabrukerdialog";
    public static final String MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD = EnvironmentUtils.getRequiredProperty("no.nav.modig.security.systemuser.password", "SRVMODIABRUKERDIALOG_PASSWORD");
}
