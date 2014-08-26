package no.nav.sbl.modiabrukerdialog.pip.geografisk;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.config.GeografiskPipConfig;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * PIP for henting av lokal og fylkes enheter for en saksbehandler.
 */
public class EnhetAttributeLocator extends AttributeLocator {

	public static final URI STRING_TYPE = URI.create("http://www.w3.org/2001/XMLSchema#string");
	public static final URI SUBJECT_CATEGORY = URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT);
	public static final URI SUBJECT_ID = URI.create(XACMLConstants.ATTRIBUTEID_SUBJECT_ID);
	public static final URI ATTRIBUTEID_LOCAL_ENHET = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet");
	public static final URI ATTRIBUTEID_FYLKESENHET = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:fylkesenhet");
	public static final URI ATTRIBUTEID_ROLLE = URI.create("urn:oasis:names:tc:xacml:2.0:subject:role");


	private EnhetAttributeLocatorDelegate delegate;

	public EnhetAttributeLocator() {
		this.attributeDesignatorSupported = true;
		this.attributeSelectorSupported = true;
		this.designatorTypes.add(Integer.valueOf(0));
		ApplicationContext context = new AnnotationConfigApplicationContext(GeografiskPipConfig.class);
		delegate = context.getBean(EnhetAttributeLocatorDelegate.class);
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
		Set<AttributeValue> values = Collections.EMPTY_SET;

		if (attributeId.equals(ATTRIBUTEID_LOCAL_ENHET)) {
			values = convertSet(delegate.getLokalEnheterForAnsatt(subjectId));
		} else if (attributeId.equals(ATTRIBUTEID_FYLKESENHET)) {
			values = convertSet(delegate.getFylkesenheterForAnsatt(subjectId));
		}

		return new EvaluationResult(new BagAttribute(attributeType, values));
	}

	private Set<AttributeValue> convertSet(Set<String> inputSet) {
		Set<AttributeValue> outputSet = new HashSet<>(inputSet.size());
		for (String string : inputSet) {
			outputSet.add(JBossXACMLUtil.getAttributeValue(string));
		}
		return outputSet;
	}

	private String getSubjectId(EvaluationCtx context) {
		return (String) context.getSubjectAttribute(STRING_TYPE, SUBJECT_ID, SUBJECT_CATEGORY).getAttributeValue().getValue();
	}
}
