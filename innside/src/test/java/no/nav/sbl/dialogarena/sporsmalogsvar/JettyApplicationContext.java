package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.JaxWsFeatures;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.Tjenesteoppsett;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import({
        JaxWsFeatures.Mock.class,
        Tjenesteoppsett.class,
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
