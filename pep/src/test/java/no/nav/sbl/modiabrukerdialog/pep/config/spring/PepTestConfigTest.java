package no.nav.sbl.modiabrukerdialog.pep.config.spring;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PepTestConfig.class})
public class PepTestConfigTest {

	@Inject
	private EnforcementPoint pep;

	@Test
	public void testInject() {
		assertTrue(pep != null);
	}
}
