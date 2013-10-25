package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        MockContext.class
})
public class MockContextBeans {

    private static final Logger LOG = LoggerFactory.getLogger(MockContextBeans.class);
    @Bean
    public WicketApplication modiaApplication() {
        LOG.debug("MockContextBeans modiaapp");
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
