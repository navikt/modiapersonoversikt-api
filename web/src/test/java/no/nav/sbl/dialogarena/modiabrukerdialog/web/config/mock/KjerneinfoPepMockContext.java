package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.response.PolicyResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KjerneinfoPepMockContext {

    @Bean(name = "kjerneinfoPep")
    public EnforcementPoint kjerneinfoPep() {
        return new EnforcementPoint() {
            @Override
            public void assertAccess(PolicyRequest policyRequest) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean hasAccess(PolicyRequest policyRequest) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public PolicyResponse evaluate(PolicyRequest policyRequest) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
