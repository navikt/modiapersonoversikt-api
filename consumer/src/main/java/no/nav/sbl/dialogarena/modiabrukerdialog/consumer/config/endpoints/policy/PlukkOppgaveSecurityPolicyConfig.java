package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.policy;

import no.nav.modig.security.tilgangskontroll.policy.enrichers.EnvironmentRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.SecurityContextRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

import static java.util.Arrays.asList;

@Configuration
public class PlukkOppgaveSecurityPolicyConfig {

    @Bean
    public EnforcementPoint plukkOppgavePep() {
        PEPImpl pep = new PEPImpl(plukkOppgavePdp());
        pep.setRequestEnrichers(asList(new SecurityContextRequestEnricher(), new EnvironmentRequestEnricher()));
        return pep;
    }

    public DecisionPoint plukkOppgavePdp() {
        URL url;
        try {
            url = new ClassPathResource("config/modiabrukerdialog-plukkoppgave-policy-config.xml").getURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PicketLinkDecisionPoint(url);
    }

}
