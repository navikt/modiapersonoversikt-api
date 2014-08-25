package no.nav.sbl.modiabrukerdialog.pep.config.spring;

import no.nav.modig.security.tilgangskontroll.config.AccessControlInterceptorConfig;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.EnvironmentRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.SecurityContextRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl;
import no.nav.sbl.modiabrukerdialog.pdp.config.spring.PdpConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

@Configuration
@Import(value = { AccessControlInterceptorConfig.class, PdpConfig.class })
public class PepConfig {
	/*
	 * Tilgang håndheves i PEP - Policy Enforcement Point. PEP kjenner ikke til hvilke vilkår som gjelder for tilgangskontroll
	 * og bygger opp en request med informasjon om bruker, ressurs, aksjon og miljø og sender denne til PDP - Policy Decision
	 * Point.
	 */
	@Inject
	@Named("modiapdp")
	private DecisionPoint modiaPdp;

	@Bean(name="pep")
	public EnforcementPoint pep() {
		PEPImpl pep = new PEPImpl(modiaPdp);
		pep.setRequestEnrichers(Arrays.asList(new SecurityContextRequestEnricher(), new EnvironmentRequestEnricher()));
		return pep;
	}
}
