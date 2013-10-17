package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.response.PolicyResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KjerneinfoPepMockContext.class)
public class HentPersonPanelMockContext {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new PersonKjerneinfoServiceBi() {
            @Override
            public HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public PingResult ping() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Bean(name = "pep")
    public EnforcementPoint pep() {
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
