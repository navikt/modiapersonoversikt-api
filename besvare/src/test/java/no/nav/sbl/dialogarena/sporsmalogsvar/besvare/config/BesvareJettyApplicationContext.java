package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.web.BesvareSporsmalApplication;

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
