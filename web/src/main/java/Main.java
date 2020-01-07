
import no.nav.apiapp.ApiApp;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.common.utils.NaisUtils;
import no.nav.sbl.dialogarena.common.abac.pep.CredentialConstants;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap.LdapContextProvider;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.ModiaApplicationContext;
import no.nav.sbl.util.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.KJERNEINFO_TJENESTEBUSS_PASSWORD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NorgEndpointFelles.KJERNEINFO_TJENESTEBUSS_USERNAME;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class Main {
    private static final String VAULT_APPLICATION_PROPERTIES_PATH = "/var/run/secrets/nais.io/vault/application.properties";
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws FileNotFoundException {
        loadVaultSecrets();
        loadPropertiesFile(
                getRequiredProperty("NAIS_NAMESPACE"),
                getRequiredProperty("NAIS_CLUSTER_NAME")
        );

        // Overstyrer appnavn slik at vi er sikre på at vi later som vi er modiabrukerdialog. ;)
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiabrukerdialog", PUBLIC);

        ApiApp.runApp(ModiaApplicationContext.class, args);
    }

    private static void loadVaultSecrets() throws FileNotFoundException {
        NaisUtils.Credentials serviceUser = NaisUtils.getCredentials("service_user");
        EnvironmentUtils.setProperty(CredentialConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(CredentialConstants.SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);
        EnvironmentUtils.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(SecurityConstants.SYSTEMUSER_PASSWORD, serviceUser.password, SECRET);

        NaisUtils.Credentials ldapUser = NaisUtils.getCredentials("ldap_user");
        EnvironmentUtils.setProperty(LdapContextProvider.LDAP_USERNAME, ldapUser.username, PUBLIC);
        EnvironmentUtils.setProperty(LdapContextProvider.LDAP_PASSWORD, ldapUser.password, SECRET);

        NaisUtils.Credentials gosysUser = NaisUtils.getCredentials("gosys_user");
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_USERNAME, gosysUser.username, PUBLIC);
        EnvironmentUtils.setProperty(KJERNEINFO_TJENESTEBUSS_PASSWORD, gosysUser.password, SECRET);

        loadFromInputStream(new FileInputStream(VAULT_APPLICATION_PROPERTIES_PATH), true);
    }

    private static void loadPropertiesFile(String namespace, String cluster) {
        if ("prod-fss".equalsIgnoreCase(cluster)) {
            loadFromResource("configurations/p.properties");
        } else if ("q0".equalsIgnoreCase(namespace)) {
            // Laster q0 data fra naiserator fil
            // loadFromResource("configurations/q0.properties");
        } else if ("q1".equalsIgnoreCase(namespace)) {
            loadFromResource("configurations/q1.properties");
        } else if ("q6".equalsIgnoreCase(namespace)) {
            loadFromResource("configurations/q6.properties");
        } else {
            loadFromResource("configurations/q0.properties");
        }
    }

    private static void loadFromResource(String resource) {
        log.info("Laster properties fra: " + resource);
        InputStream propsResource = Main.class.getClassLoader().getResourceAsStream(resource);
        if (propsResource == null) {
            throw new RuntimeException(resource);
        }
        loadFromInputStream(propsResource, false);
    }

    private static void loadFromInputStream(InputStream is, boolean secrets) {
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke lese properties", e);
        }

        Properties target = System.getProperties();

        for (String name : new HashSet<>(props.stringPropertyNames())) {
            String value = props.getProperty(name);
            String safeValue = secrets ? "*********" : value;
            if (target.containsKey(name)) {
                log.warn("Old value '{}' is replaced with", target.getProperty(name));
                log.warn("{} = {}", name, safeValue);
            } else {
                log.info("Setting {} = {}", name, safeValue);
            }

            target.setProperty(name, value);
        }
    }
}