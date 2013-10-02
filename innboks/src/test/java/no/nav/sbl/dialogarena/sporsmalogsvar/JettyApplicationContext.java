package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.TestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(TestContext.class)
public class JettyApplicationContext {

    @Bean
    public InnboksTestApplication application() {
        return new InnboksTestApplication();
    }
}
