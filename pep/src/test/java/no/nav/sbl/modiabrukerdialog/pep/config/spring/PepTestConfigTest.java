package no.nav.sbl.modiabrukerdialog.pep.config.spring;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PepTestConfig.class})
public class PepTestConfigTest {

	@Inject
	private EnforcementPoint pep;

	@Test
	public void testInject() {
		assertTrue(pep != null);
	}
}
