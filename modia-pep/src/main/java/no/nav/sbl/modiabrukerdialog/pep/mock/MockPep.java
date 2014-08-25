package no.nav.sbl.modiabrukerdialog.pep.mock;

import no.nav.modig.security.tilgangskontroll.policy.attributes.values.StringValue;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
import no.nav.modig.security.tilgangskontroll.policy.response.Decision;
import no.nav.modig.security.tilgangskontroll.policy.response.PolicyResponse;

public class MockPep implements EnforcementPoint{

	public MockPep() {
	}

	@Override
	public void assertAccess(PolicyRequest request) {
	}

	/**
	 *
	 * @param request
	 * @return false if discretion code 6 or 7
	 */
	@Override
	public boolean hasAccess(PolicyRequest request) {
		for (PolicyAttribute policyAttribute : request.getAttributes()) {
			if ("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code".equals(policyAttribute.getAttributeId().getURN())
					&& ("6".equals(((StringValue) policyAttribute.getAttributeValue()).getValue())
					|| "7".equals(((StringValue) policyAttribute.getAttributeValue()).getValue()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PolicyResponse evaluate(PolicyRequest request) {
		return hasAccess(request) ? new PolicyResponse(Decision.Permit) : new PolicyResponse(Decision.Deny);
	}
}
