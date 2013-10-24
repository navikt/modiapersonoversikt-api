package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.mockito.Mockito.mock;

@Profile("test")
@Configuration
@Import({
        MockContext.class
})
public class MockContextBeans {

    @Bean
    public WicketApplication modiaApplication() {
        System.out.println("MockContextBeans modiaapp");
        return new WicketApplication();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
        return mock(EnforcementPoint.class);
    }

    @Bean(name = "kjerneinfoPep")
    public EnforcementPoint kjerneinfoPep() {
        return mock(EnforcementPoint.class);
    }
}
