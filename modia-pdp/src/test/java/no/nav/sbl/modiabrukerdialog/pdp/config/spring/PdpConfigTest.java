package no.nav.sbl.modiabrukerdialog.pdp.config.spring;

import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import org.junit.Test;

/**
 * Created by b134338 on 25.08.14.
 */
public class PdpConfigTest {
	@Test
	public void testModiaPdp() throws Exception {
		PdpConfig pdpConfig = new PdpConfig();
		DecisionPoint decisionPoint = pdpConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}

	@Test
	public void testModiaTestPdp() throws Exception {
		PdpTestConfig pdpConfig = new PdpTestConfig();
		DecisionPoint decisionPoint = pdpConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}
}
