package no.nav.sbl.dialogarena.besvare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JaxWsFeatures.Integration.class,
        ServicesConfig.class
})
public class BesvareSporsmalConfig {

}