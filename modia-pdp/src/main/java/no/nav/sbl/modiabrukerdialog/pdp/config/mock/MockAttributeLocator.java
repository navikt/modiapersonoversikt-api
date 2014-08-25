package no.nav.sbl.modiabrukerdialog.pdp.config.mock;

import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class MockAttributeLocator extends AttributeLocator {

	public MockAttributeLocator() {
		this.attributeDesignatorSupported = true;
		this.attributeSelectorSupported = true;

		this.designatorTypes.add(Integer.valueOf(0));
		this.designatorTypes.add(Integer.valueOf(1));
		this.designatorTypes.add(Integer.valueOf(2));
	}

	@Override
	public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context,
	                                      int designatorType) {
		if (!this.ids.contains(attributeId)) {
			if (attributeType != null) {
				return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
			}
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
		}

		Set<AttributeValue> values = new HashSet<>();

		return new EvaluationResult(new BagAttribute(attributeType, values));
	}
}
