package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

public class RestConstants {
    public static final String PERSONDOKUMENTER_BASEURL = System.getProperty("persondokumenter.api.url");
    public static final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    public static final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    public static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    public static final String TEMA_HEADER = "Tema";
    public static final String OPPLYSNINGSTYPER_HEADER = "Opplysningstyper";
    public static final String ALLE_TEMA_HEADERVERDI = "GEN";
    public static final String OPPLYSNINGSTYPER_HEADERVERDI = "KontaktinformasjonForDoedsbo";

    public static final String BASIC_AUTH_SEPERATOR = ":";
    public static final String AUTH_SEPERATOR = " ";
    public static final String AUTH_METHOD_BEARER = "Bearer";
    public static final String AUTH_METHOD_BASIC = "Basic";

    public static final String SECURITY_TOKEN_SERVICE_BASEURL = System.getProperty("sts.token.api.url");
    public static final String STS_USERNAME_PW_QUERY_PARAMETERS = "?grant_type=client_credentials&scope=openid";
    public static final String OIDC_EXPIRARY_URI = "exp";

    public static final String MODIABRUKERDIALOG_SYSTEM_USER = "srvModiabrukerdialog";
    public static final String MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD = System.getProperty("no.nav.modig.security.systemuser.password");

    public static final String SAF_GRAPHQL_BASEURL = System.getProperty("saf.graphql.url");
    public static final String SAF_HENTDOKUMENT_BASEURL = System.getProperty("saf.hentdokument.url");
}
