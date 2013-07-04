package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class WicketTesterConfig {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<WicketApplication>(application);
    }

}
