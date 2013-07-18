package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.JaxWsFeatures;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServicesConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JaxWsFeatures.Integration.class,
        ServicesConfig.class
})
public class BesvareSporsmalConfig {

}