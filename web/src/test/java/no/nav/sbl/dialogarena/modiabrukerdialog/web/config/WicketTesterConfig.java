package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WicketTesterConfig {

    @Bean
    public WebApplication modiaApplication() {
        return new ModiaTestApplication();
    }

    @Bean
    public FluentWicketTester<WebApplication> fluentWicketTester() {
        return new FluentWicketTester<>(modiaApplication());
    }

}
