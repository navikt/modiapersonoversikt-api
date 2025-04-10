package no.nav.modiapersonoversikt;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import no.nav.common.utils.Credentials;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.common.utils.NaisUtils;
import no.nav.common.utils.SslUtils;
import no.nav.modiapersonoversikt.utils.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import static no.nav.common.utils.EnvironmentUtils.Type.PUBLIC;
import static no.nav.common.utils.EnvironmentUtils.Type.SECRET;
import static no.nav.modiapersonoversikt.utils.AppConstants.SYSTEMUSER_PASSWORD_PROPERTY;
import static no.nav.modiapersonoversikt.utils.AppConstants.SYSTEMUSER_USERNAME_PROPERTY;

@SpringBootApplication
public class ProxyMain {
    static {
        System.setProperty("javax.xml.soap.SAAJMetaFactory", "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
    }

    public static void main(String... args) {
        loadVaultSecrets();
        SslUtils.setupTruststore();
        EnvironmentUtils.setProperty("NAIS_APP_NAME", "modiapersonoversiktproxy", PUBLIC);
        MetricsConfig.setup();
        SpringApplication.run(ProxyMain.class, args);
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
