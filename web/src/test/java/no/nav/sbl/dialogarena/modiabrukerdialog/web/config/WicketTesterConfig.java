package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

public class WicketTesterConfig {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester fluentWicketTester() {
        return new FluentWicketTester(application);
    }



}
