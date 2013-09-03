package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

public class JettyApplicationContext {

    @Bean
    public InnboksTestApplication application() {
        return new InnboksTestApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
