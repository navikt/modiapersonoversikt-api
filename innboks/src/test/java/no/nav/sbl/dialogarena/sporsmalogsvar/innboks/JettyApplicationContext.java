package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.config.TestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(TestContext.class)
public class JettyApplicationContext {

    @Bean
    public InnboksTestApplication application() {
        return new InnboksTestApplication();
    }
}
