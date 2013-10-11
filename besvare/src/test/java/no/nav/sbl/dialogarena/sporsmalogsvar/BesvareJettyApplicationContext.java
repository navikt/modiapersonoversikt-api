package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.TjenesterMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import(TjenesterMock.class)
public class BesvareJettyApplicationContext {

    @Bean
    public BesvareSporsmalApplication application() {
        return new BesvareSporsmalApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
