package no.nav.sbl.modiabrukerdialog.pdp.config.spring;

import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import org.junit.Test;

public class PdpConfigTest {
	@Test
	public void testModiaPdp() throws Exception {
		PdpConfig pdpConfig = new PdpConfig();
		DecisionPoint decisionPoint = pdpConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}
	@Test
	public void testModiaTestPdp() throws Exception {
		PdpTestConfig pdpTestConfig = new PdpTestConfig();
		DecisionPoint decisionPoint = pdpTestConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void testModiaPdpFNF() throws Exception {
		PdpConfig pdpConfig = new PdpConfig(){
			@Override
			public DecisionPoint modiaPdp() {
				return new PicketLinkDecisionPoint(getConfigUrl("null"));
			}
		};
		DecisionPoint decisionPoint = pdpConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void testModiaTestPdpFNF() throws Exception {
		PdpTestConfig pdpConfig = new PdpTestConfig(){
			@Override
			public DecisionPoint modiaPdp() {
				return new PicketLinkDecisionPoint(getConfigUrl("null"));
			}
		};
		DecisionPoint decisionPoint = pdpConfig.modiaPdp();

		decisionPoint.evaluate(new PolicyRequest());
	}
}
