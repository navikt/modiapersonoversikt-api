package no.nav.sbl.dialogarena.varsel.context;

import no.nav.sbl.dialogarena.varsel.VarselApplication;
import no.nav.sbl.dialogarena.varsel.lamell.context.VarselLamellContext;
import no.nav.sbl.dialogarena.varsel.config.VarselPortTypeTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({VarselLamellContext.class, VarselPortTypeTestConfig.class})
public class JettyApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public VarselApplication application() {
        return new VarselApplication();
    }
}
