package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksTestApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import(WicketApplicationTestContext.class)
public class WicketTesterConfig {

    @Inject
    private InnboksTestApplication innboksTestApplication;

    @Bean
    public FluentWicketTester<InnboksTestApplication> wicket() {
        return new FluentWicketTester<>(innboksTestApplication);
    }

}
