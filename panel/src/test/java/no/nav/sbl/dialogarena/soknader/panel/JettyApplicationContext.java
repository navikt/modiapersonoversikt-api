package no.nav.sbl.dialogarena.soknader.panel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class JettyApplicationContext {

    @Bean
    public SoeknaderTestApplication application() {
        return new SoeknaderTestApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
