package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
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

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "kjerneinfoPep")
    public EnforcementPoint kjerneinfoPep() {
        return mock(EnforcementPoint.class);
    }
}
