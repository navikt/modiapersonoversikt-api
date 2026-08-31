package no.nav.modiapersonoversikt;

import no.nav.common.utils.EnvironmentUtils;

import no.nav.common.utils.SslUtils;
import no.nav.modiapersonoversikt.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;

@SpringBootApplication
public class Main {
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }

    public static void main(String... args) {
        SslUtils.setupTruststore();
        // Overstyrer appnavn slik at vi er sikre på at vi later som vi er modiabrukerdialog. ;)
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiabrukerdialog", PUBLIC);
        MetricsConfig.setup();
        SpringApplication.run(Main.class, args);
    }
}
