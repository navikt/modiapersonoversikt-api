package no.nav.sbl.modiabrukerdialog.pep.mock;

import no.nav.modig.security.tilgangskontroll.URN;
import no.nav.modig.security.tilgangskontroll.policy.attributes.values.StringValue;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.ResourceAttribute;
import no.nav.modig.security.tilgangskontroll.policy.response.Decision;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MockPepTest {
	MockPep mockPep;

	@Before
	public void setUp() {
		mockPep = new MockPep();
	}

	@Test
	public void testAssertAccess() throws Exception {
		mockPep.assertAccess(new PolicyRequest());
	}

	@Test
	public void testHasAccessFalse() throws Exception {
		PolicyRequest policyRequest = new PolicyRequest();
		policyRequest = policyRequest.copyAndAppend(
				new ResourceAttribute(new URN("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code"),
						new StringValue("6")));

		assertTrue(mockPep.evaluate(policyRequest).decision().equals(Decision.Deny));
	}

	@Test
	public void testHasAccessTrue() throws Exception {
		PolicyRequest policyRequest = new PolicyRequest();
		policyRequest = policyRequest.copyAndAppend(
				new ResourceAttribute(new URN("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code"),
						new StringValue("0")));

		assertTrue(mockPep.evaluate(policyRequest).decision().equals(Decision.Permit));
	}
}
