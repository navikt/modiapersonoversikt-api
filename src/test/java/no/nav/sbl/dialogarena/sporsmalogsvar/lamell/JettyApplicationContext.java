package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.context.SporsmalOgSvarContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.config.TestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TestContext.class, SporsmalOgSvarContext.class})
public class JettyApplicationContext {

    @Bean
    public InnboksTestApplication application() {
        return new InnboksTestApplication();
    }
}
