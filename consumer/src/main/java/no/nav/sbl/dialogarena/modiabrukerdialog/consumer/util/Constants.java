package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.tjenester.person.oppslag.v1.domain.Opplysningstype;

public class Constants {
    public static final String NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token";
    public static final String NAV_PERSONIDENT_HEADER = "Nav-Personident";
    public static final String NAV_CALL_ID_HEADER = "Nav-Call-Id";
    public static final String TEMA_HEADER = "Tema";
    public static final String OPPLYSNINGSTYPER_HEADER = "Opplysningstyper";

    public static final String ALLE_TEMA_HEADERVERDI = "GEN";
    public static final String OPPLYSNINGSTYPER_HEADERVERDI = Opplysningstype.UTENLANDSK_IDENTIFIKASJONSNUMMER;

    public static final String BASIC_AUTH_SEPERATOR = ":";

    public static final String SECURITY_TOKEN_SERVICE_BASEURL = System.getProperty("sts.token.api.url");

    public static final String MODIABRUKERDIALOG_SYSTEM_USER = "srvModiabrukerdialog";
    public static final String MODIABRUKERDIALOG_SYSTEM_USER_PASSWORD = System.getProperty("no.nav.modig.security.systemuser.password");

    public static final String PERSONDOKUMENTER_BASEURL = System.getProperty("persondokumenter.api.url");
}
