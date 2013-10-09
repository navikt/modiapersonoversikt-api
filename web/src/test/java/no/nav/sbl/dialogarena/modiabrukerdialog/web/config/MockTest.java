package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockTest {

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
    }

    @Bean
    public PersonKjerneinfoServiceBi mock() {
        return Mockito.mock(PersonKjerneinfoServiceBi.class);
    }

    @Bean(name = "kjerneinfoPep")
    public EnforcementPoint enforcementPointMock() {
        return Mockito.mock(EnforcementPoint.class);
    }

    @Bean(name = "pep")
    public EnforcementPoint pepMock() {
        return Mockito.mock(EnforcementPoint.class);
    }

}
