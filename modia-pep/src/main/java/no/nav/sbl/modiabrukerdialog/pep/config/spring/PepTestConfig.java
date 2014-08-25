package no.nav.sbl.modiabrukerdialog.pep.config.spring;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.modiabrukerdialog.pep.mock.MockPep;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

public class PepTestConfig {
	/*
	 * Tilgang håndheves i PEP - Policy Enforcement Point. PEP kjenner ikke til hvilke vilkår som gjelder for tilgangskontroll
	 * og bygger opp en request med informasjon om bruker, ressurs, aksjon og miljø og sender denne til PDP - Policy Decision
	 * Point.
	 */
	@Bean
	@Named("pep")
	public EnforcementPoint pep() {
		return new MockPep();
	}
}
