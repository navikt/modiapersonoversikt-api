package no.nav.modiapersonoversikt;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import no.nav.common.utils.EnvironmentUtils;

import no.nav.common.utils.SslUtils;
import no.nav.modiapersonoversikt.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder()  {
        return builder -> builder.modulesToInstall(new JodaModule()).build();
    }
}
