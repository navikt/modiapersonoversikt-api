package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.jetty;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        MockContext.class,

})
public class NoIntegrationContext {

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
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
