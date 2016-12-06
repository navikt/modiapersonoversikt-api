package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;


import no.nav.sbl.modiabrukerdialog.pip.geografisk.EnhetAttributeLocator;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.*;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class MockAttributeLocator extends AttributeLocator {

	private static final URI STRING_TYPE = URI.create("http://www.w3.org/2001/XMLSchema#string");
	private static final URI SUBJECT_CATEGORY = URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT);
	private static final URI SUBJECT_ID = URI.create(XACMLConstants.ATTRIBUTEID_SUBJECT_ID);

	public MockAttributeLocator() {
		this.attributeDesignatorSupported = true;
		this.attributeSelectorSupported = true;
		this.ids.add(EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET);
		this.ids.add(EnhetAttributeLocator.ATTRIBUTEID_FYLKESENHET);
		this.ids.add(EnhetAttributeLocator.ATTRIBUTEID_GEOGRAFISK_NEDSLAGSFELT);
		this.ids.add(EnhetAttributeLocator.ATTRIBUTEID_ROLLE);

		this.designatorTypes.add(0);
	}

	@Override
	public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context, int designatorType) {
		if (!this.ids.contains(attributeId)) {
			if (attributeType != null) {
				return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
			}
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
		}

		String subjectId = getSubjectId(context);
		Set<AttributeValue> values = new HashSet<>();

		if(subjectId != null) {
			if (attributeId.equals(EnhetAttributeLocator.ATTRIBUTEID_ROLLE)) {
				addRoleValue(subjectId, values);
			} else if (attributeId.equals(EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET)) {
				addEnhetValue(subjectId, values);
			} else if (attributeId.equals(EnhetAttributeLocator.ATTRIBUTEID_FYLKESENHET)) {
				addEnhetValue(subjectId, values);
			} else if (attributeId.equals(EnhetAttributeLocator.ATTRIBUTEID_GEOGRAFISK_NEDSLAGSFELT)){
				addEnhetValue(subjectId, values);
			}
		}

		return new EvaluationResult(new BagAttribute(attributeType, values));
	}

	private void addRoleValue(String subjectId, Set<AttributeValue> values) {
		if (subjectId.endsWith("900001")) {
			values.add(JBossXACMLUtil.getAttributeValue("0000-GA-GOSYS-NASJONAL"));
			values.add(JBossXACMLUtil.getAttributeValue("0000-GA-BD06_EndreKontonummer"));
			values.add(JBossXACMLUtil.getAttributeValue("0000-GA-BD06_EndreKontaktAdresse"));
			values.add(JBossXACMLUtil.getAttributeValue("0000-X-KONTONUMMER_ENDRER"));
		} else if (subjectId.endsWith("900002")) {
			values.add(JBossXACMLUtil.getAttributeValue("0000-GA-GOSYS-UTVIDBAR_TIL_REGIONAL"));
		}
	}

	private void addEnhetValue(String subjectId, Set<AttributeValue> values) {
		if (subjectId.endsWith("900002")) {
			values.add(JBossXACMLUtil.getAttributeValue("1234"));
			values.add(JBossXACMLUtil.getAttributeValue("0313"));
		} else if (subjectId.endsWith("900003")) {
			values.add(JBossXACMLUtil.getAttributeValue("0313"));
		}
	}

	private String getSubjectId(EvaluationCtx context) {
		return (String) context.getSubjectAttribute(STRING_TYPE, SUBJECT_ID, SUBJECT_CATEGORY).getAttributeValue().getValue();
	}
}
