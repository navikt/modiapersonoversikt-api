package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KjerneinfoPepMockContext {

    @Bean(name = "kjerneinfoPep")
    public EnforcementPoint kjerneinfoPep() {
        return mock(EnforcementPoint.class);
    }

}
