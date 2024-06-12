package no.nav.modiapersonoversikt;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import no.nav.common.utils.Credentials;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.common.utils.NaisUtils;

import no.nav.common.utils.SslUtils;
import no.nav.modiapersonoversikt.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;
import static no.nav.common.utils.EnvironmentUtils.Type.SECRET;
import static no.nav.modiapersonoversikt.config.AppConstants.SYSTEMUSER_PASSWORD_PROPERTY;
import static no.nav.modiapersonoversikt.config.AppConstants.SYSTEMUSER_USERNAME_PROPERTY;

@SpringBootApplication
public class Main {
    public static final List<String> GCP_CLUSTERS = List.of("dev-gcp", "prod-gcp");
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }

    public static void main(String... args) {
        String clusterName = EnvironmentUtils.getRequiredProperty("NAIS_CLUSTER_NAME");
        if(!GCP_CLUSTERS.contains(clusterName)) {
            loadVaultSecrets();
        }
        SslUtils.setupTruststore();
        // Overstyrer appnavn slik at vi er sikre på at vi later som vi er modiabrukerdialog. ;)
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiabrukerdialog", PUBLIC);
        MetricsConfig.setup();
        SpringApplication.run(Main.class, args);
    }

    private static void loadVaultSecrets() {
        Credentials serviceUser = NaisUtils.getCredentials("service_user");
        EnvironmentUtils.setProperty(SYSTEMUSER_USERNAME_PROPERTY, serviceUser.username, PUBLIC);
        EnvironmentUtils.setProperty(SYSTEMUSER_PASSWORD_PROPERTY, serviceUser.password, SECRET);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder()  {
        return builder -> builder.modulesToInstall(new JodaModule()).build();
    }
}
