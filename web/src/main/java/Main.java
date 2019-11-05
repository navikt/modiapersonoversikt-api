
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
//        loadVaultSecrets();
        loadPropertiesFile(getEnvVar("NAIS_NAMESPACE"));

        ApiApp.runApp(ModiaApplicationContext.class, args);
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