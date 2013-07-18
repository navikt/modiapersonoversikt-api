package no.nav.sbl.dialogarena.besvare;

import no.nav.sbl.dialogarena.besvare.config.JaxWsFeatures;
import no.nav.sbl.dialogarena.besvare.config.ServicesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import({
        JaxWsFeatures.Mock.class,
        ServicesConfig.class
})
public class JettyApplicationContext {

    @Bean
    public BesvareSporsmalApplication application() {
        return new BesvareSporsmalApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
