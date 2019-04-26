
import no.nav.apiapp.ApiApp;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.util.stream.Collectors.toSet;

public class Main {
    private static final String VAULT_APPLICATION_PROPERTIES_PATH = "/var/run/secrets/nais.io/vault/application.properties";
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        loadVaultSecrets();
        loadPropertiesFile(getEnvVar("NAIS_NAMESPACE"));

        ApiApp.startApp(ModiaApplicationContext.class, args);
    }

    private static void loadVaultSecrets() {
        Properties props = new Properties();
        try {
            InputStream stream = new FileInputStream(VAULT_APPLICATION_PROPERTIES_PATH);
            props.load(stream);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        Properties target = System.getProperties();

        for (String name : props.stringPropertyNames().stream().collect(toSet())) {
            log.info("Laster vault secret " + name);
            String value = props.getProperty(name);
            target.setProperty(name, value);
        }
    }

    private static void loadPropertiesFile(String naisNamespace) {
        switch(naisNamespace) {
            case "q6":
                loadFromResource("configurations/q6.properties");
            default:
                loadFromResource("configurations/q6.properties");
        }
    }

    private static void loadFromResource(String resource) {
        InputStream propsResource = Main.class.getClassLoader().getResourceAsStream(resource);
        if (propsResource == null) {
            throw new RuntimeException(resource);
        }
        Properties props = new Properties();

        try {
            props.load(propsResource);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke lese properties", e);
        }

        Properties target = System.getProperties();

        for (String name : props.stringPropertyNames().stream().collect(toSet())) {
            String value = props.getProperty(name);
            target.setProperty(name, value);
        }
    }

    private static String getEnvVar(String s) {
        String var = System.getenv(s);
        if (var == null) {
            return System.getProperty(s);
        }
        return var;
    }
}

/*
        setProperty("isso.isalive.url", getEnvVar("ISSO_ISALIVE_URL"), PUBLIC);
        setProperty("openAMTokenIssuer.url", getEnvVar("ISSO_JWKS_URL"), PUBLIC);
        setProperty("isso-jwks.url", getEnvVar("ISSO_JWKS_URL"), PUBLIC);
        setProperty("isso-issuer.url", getEnvVar("ISSO_ISSUER_URL"), PUBLIC);
        setProperty("isso-host.url", getEnvVar("ISSO_HOST_URL"), PUBLIC);
        setProperty("oidc-redirect.url", getEnvVar("OIDC_REDIRECT_URL_URL"), PUBLIC);
        setProperty("server.gosys.url", getEnvVar("SERVER_GOSYS_URL"), PUBLIC);
        setProperty("server.pesys.url", getEnvVar("SERVER_PESYS_URL"), PUBLIC);
        setProperty("server.aktivitetsplan.url", getEnvVar("SERVER_AKTIVITETSPLAN_URL"), PUBLIC);
        setProperty("server.norg2-frontend.url", getEnvVar("SERVER_NORG2_FRONTEND_URL"), PUBLIC);
        setProperty("server.drek.url", getEnvVar("SERVER_DREK_URL"), PUBLIC);
        setProperty("server.veilarbportefoljeflatefs.url", getEnvVar("SERVER_VEILARBPORTEFOLJEFLATEFS_URL"), PUBLIC);

        setProperty("foreldrepengerendpoint.url", getEnvVar("VIRKSOMHET_FORELDREPENGER_V2_ENDPOINTURL"), PUBLIC);
        setProperty("utbetalingendpoint.url", getEnvVar("UTBETALING_V1_ENDPOINTURL"), PUBLIC);
        setProperty("dkifendpoint.url", getEnvVar("VIRKSOMHET_DIGITALKONTAKINFORMASJON_V1_ENDPOINTURL"), PUBLIC);
        setProperty("oppfolgingendpoint.url", getEnvVar("VIRKSOMHET_OPPFOLGING_V1_ENDPOINTURL"), PUBLIC);
        setProperty("sykepengerendpoint.url", getEnvVar("VIRKSOMHET_SYKEPENGER_V2_ENDPOINTURL"), PUBLIC);
        setProperty("pleiepengerendpoint.url", getEnvVar("VIRKSOMHET_PLEIEPENGER_V1_ENDPOINTURL"), PUBLIC);
        setProperty("organisasjonendpoint.v4.url", getEnvVar("VIRKSOMHET_ORGANISASJON_V4_ENDPOINTURL"), PUBLIC);
        setProperty("kodeverkendpoint.v2.url", getEnvVar("VIRKSOMHET_KODEVERK_V2_ENDPOINTURL"), PUBLIC);
        setProperty("personsokendpoint.url", getEnvVar("VIRKSOMHET_PERSONSOK_V1_ENDPOINTURL"), PUBLIC);
        setProperty("server.arena.url", getEnvVar("SERVER_ARENA_URL"), PUBLIC);
        setProperty("server.aareg.url", getEnvVar("SERVER_AAREG_URL"), PUBLIC);
        setProperty("tjenestebuss.url", getEnvVar("TJENESTEBUSS_URL"), PUBLIC);
        setProperty("ytelseskontraktendpoint.url", getEnvVar("VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL"), PUBLIC);
        setProperty("behandlebrukerprofilendpoint.url", getEnvVar("VIRKSOMHET_BEHANDLEBRUKERPROFIL_V2_ENDPOINTURL"), PUBLIC);
        setProperty("gsak.oppgave.v3.url", getEnvVar("VIRKSOMHET_OPPGAVE_V3_ENDPOINTURL"), PUBLIC);
        setProperty("gsak.oppgavebehandling.v3.url", getEnvVar("VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL"), PUBLIC);
        setProperty("gsak.tildeloppgave.v1.url", getEnvVar("VIRKSOMHET_TILDELOPPGAVE_V1_ENDPOINTURL"), PUBLIC);
        setProperty("gsak.sak.v1.url", getEnvVar("VIRKSOMHET_SAK_V1_ENDPOINTURL"), PUBLIC);
        setProperty("pensjon.sak.v1.url", getEnvVar("PENSJON_PENSJONSAK_V1_ENDPOINTURL"), PUBLIC);
        setProperty("gsak.behandlesak.v1.url", getEnvVar("VIRKSOMHET_BEHANDLESAK_V1_ENDPOINTURL"), PUBLIC);
        setProperty("arena.arbeidogaktivitet.v1.url", getEnvVar("VIRKSOMHET_ARBEIDOGAKTIVITET_V1_ENDPOINTURL"), PUBLIC);
        setProperty("aktorid.ws.url", getEnvVar("AKTOER_V1_ENDPOINTURL"), PUBLIC);
        setProperty("appres.cms.url", getEnvVar("APPRES_CMS_URL"), PUBLIC);
        setProperty("modiabrukerdialog.standardtekster.tilbakemelding.url", getEnvVar("MODIABRUKERDIALOG_STANDARDTEKSTER_TILBAKEMELDING_URL"), PUBLIC);
        setProperty("sakogbehandling.ws.url", getEnvVar("SAKOGBEHANDLING_ENDPOINTURL"), PUBLIC);
        setProperty("henvendelse.v2.url", getEnvVar("DOMENE_BRUKERDIALOG_HENVENDELSE_V2_ENDPOINTURL"), PUBLIC);
        setProperty("send.ut.henvendelse.url", getEnvVar("DOMENE_BRUKERDIALOG_SENDUTHENVENDELSE_V1_ENDPOINTURL"), PUBLIC);
        setProperty("behandle.henvendelse.url", getEnvVar("DOMENE_BRUKERDIALOG_BEHANDLEHENVENDELSE_V1_ENDPOINTURL"), PUBLIC);
        setProperty("henvendelser.ws.url", getEnvVar("DOMENE_BRUKERDIALOG_HENVENDELSESOKNADERSERVICE_V1_ENDPOINTURL"), PUBLIC);
        setProperty("varsler.ws.url", getEnvVar("BRUKERVARSELV1_ENDPOINTURL"), PUBLIC);
        setProperty("journal.v2.url", getEnvVar("JOURNAL_V2_ENDPOINTURL"), PUBLIC);
        setProperty("innsyn.journal.v2.url", getEnvVar("INNSYNJOURNAL_V2_ENDPOINTURL"), PUBLIC);
        setProperty("tps.person.v3.url", getEnvVar("VIRKSOMHET_PERSON_V3_ENDPOINTURL"), PUBLIC);
        setProperty("norg2.organisasjonenhet.v2.url", getEnvVar("VIRKSOMHET_ORGANISASJONENHET_V2_ENDPOINTURL"), PUBLIC);
        setProperty("norg2.organisasjonenhetkontaktinformasjon.v1.url", getEnvVar("VIRKSOMHET_ORGANISASJONENHETKONTAKTINFORMASJON_V1_ENDPOINTURL"), PUBLIC);
        setProperty("egenansatt.v1.url", getEnvVar("VIRKSOMHET_EGENANSATT_V1_ENDPOINTURL"), PUBLIC);
        setProperty("tps.behandleperson.v1.url", getEnvVar("VIRKSOMHET_BEHANDLEPERSON_V1_ENDPOINTURL"), PUBLIC);
        setProperty("arbeidsfordeling.v1.url", getEnvVar("VIRKSOMHET_ARBEIDSFORDELING_V1_ENDPOINTURL"), PUBLIC);
        setProperty("kodeverksmapper.oppgavetype.url", getEnvVar("KODEVERKSMAPPER_OPPGAVETYPE_URL"), PUBLIC);
        setProperty("kodeverksmapper.underkategori.url", getEnvVar("KODEVERKSMAPPER_UNDERKATEGORI_URL"), PUBLIC);
        setProperty("kodeverksmapper.ping.url", getEnvVar("KODEVERKSMAPPER_PING_URL"), PUBLIC);
        setProperty("unleash.url", getEnvVar("UNLEASH_API_URL"), PUBLIC);
        setProperty("veilarboppfolging.api.url", getEnvVar("VEILARBOPPFOLGINGAPI_URL"), PUBLIC);
        setProperty("sts.token.api.url", getEnvVar("SECURITY_TOKEN_SERVICE_TOKEN_URL"), PUBLIC);
        setProperty("persondokumenter.api.url", getEnvVar("PERSON_OPPSLAG_V1_URL"), PUBLIC);

        setProperty("start.utbetaling.withmock", "false", PUBLIC);
        setProperty("start.kodeverk.withmock", "false", PUBLIC);
        setProperty("start.kjerneinfo.withmock", "false", PUBLIC);
        setProperty("modiabrukerdialog.datadir", "target/modiabrukerdialog", PUBLIC);
        setProperty("kan.purge.oppgaver", "true", PUBLIC);
        setProperty("tjenester.url", getEnvVar("TJENESTER_URL"), PUBLIC);
        setProperty("current.domain", getEnvVar("CURRENT_DOMAIN"), PUBLIC);

        setProperty("ldap.username", getEnvVar("LDAP_USERNAME"), PUBLIC);
        setProperty("ldap.password", getEnvVar("LDAP_PASSWORD"), SECRET);
        setProperty("ldap.domain", getEnvVar("LDAP_DOMAIN"), PUBLIC);
        setProperty("ldap.basedn", getEnvVar("LDAP_BASEDN"), PUBLIC);
        setProperty("ldap.url", getEnvVar("LDAP_URL"), PUBLIC);
        setProperty("ctjenestebuss.username", getEnvVar("SRV_KJERNEINFO_TJENESTEBUSS_USERNAME"), PUBLIC);
        setProperty("ctjenestebuss.password", getEnvVar("SRV_KJERNEINFO_TJENESTEBUSS_PASSWORD"), SECRET);
        setProperty("isso-rp-user.username", getEnvVar("ISSO_RP_USER_USERNAME"), PUBLIC);
        setProperty("isso-rp-user.password", getEnvVar("ISSO_RP_USER_PASSWORD"), SECRET);

        setProperty("redis.sentinelmode", getEnvVar("REDIS_SENTINELMODE"), PUBLIC);
        setProperty("redis.host", getEnvVar("REDIS_HOST"), PUBLIC); // Set by redis
        setProperty("redis.port", getEnvVar("REDIS_PORT"), PUBLIC);
 */