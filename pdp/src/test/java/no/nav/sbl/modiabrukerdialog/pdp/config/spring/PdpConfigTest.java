package no.nav.sbl.modiabrukerdialog.pdp.config.spring;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.PolicyRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

	@Test
	public void testModiaPdpFNF() throws Exception {
		assertThrows(RuntimeException.class, () -> {
			PdpConfig pdpConfig = new PdpConfig() {
				@Override
				public DecisionPoint modiaPdp() {
					return new PicketLinkDecisionPoint(getConfigUrl("null"));
				}
			};
			DecisionPoint decisionPoint = pdpConfig.modiaPdp();

			decisionPoint.evaluate(new PolicyRequest());
		});
	}

	@Test
	public void testModiaTestPdpFNF() throws Exception {
		assertThrows(RuntimeException.class, () -> {
			PdpTestConfig pdpConfig = new PdpTestConfig() {
				@Override
				public DecisionPoint modiaPdp() {
					return new PicketLinkDecisionPoint(getConfigUrl("null"));
				}
			};
			DecisionPoint decisionPoint = pdpConfig.modiaPdp();

			decisionPoint.evaluate(new PolicyRequest());
		});
	}
}
