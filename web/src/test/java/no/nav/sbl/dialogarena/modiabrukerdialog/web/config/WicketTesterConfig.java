package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.inject.Inject;

@Configuration
@PropertySource("classpath:environment-test.properties")
public class WicketTesterConfig {

    static {
        System.setProperty("tjenestebuss.url", "http://changeme");
        System.setProperty("ctjenestebuss.username", "me");
        System.setProperty("ctjenestebuss.password", "secret");
    }

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<WicketApplication>(application);
    }

}
