package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareHenvendelseConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.HenvendelseTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import({BesvareHenvendelseConfig.class, HenvendelseTestConfig.class, BesvareServiceConfig.class})
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
