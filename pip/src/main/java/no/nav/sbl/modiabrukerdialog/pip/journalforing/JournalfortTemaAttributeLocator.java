package no.nav.sbl.modiabrukerdialog.pip.journalforing;

import no.nav.sbl.modiabrukerdialog.pip.journalforing.config.JournalfortTemaPipConfig;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.JournalfortTemaAttributeLocatorDelegate;
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
import java.util.HashSet;
import java.util.Set;

public class JournalfortTemaAttributeLocator extends AttributeLocator {

    public static final URI ATTRIBUTEID_TEMA = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:tema");
    public static final URI STRING_TYPE = URI.create("http://www.w3.org/2001/XMLSchema#string");
    public static final URI SUBJECT_CATEGORY = URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT);
    public static final URI SUBJECT_ID = URI.create(XACMLConstants.ATTRIBUTEID_SUBJECT_ID);

    private JournalfortTemaAttributeLocatorDelegate delegate;

    public JournalfortTemaAttributeLocator() {
        this.attributeDesignatorSupported = true;
        this.attributeSelectorSupported = true;
        this.designatorTypes.add(0);
        ApplicationContext context = new AnnotationConfigApplicationContext(JournalfortTemaPipConfig.class);
        delegate = context.getBean(JournalfortTemaAttributeLocatorDelegate.class);
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context, int designatorType) {
        if (!this.ids.contains(attributeId)) {
            if (attributeType != null) {
                return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
            }
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
        }

        return new EvaluationResult(
                new BagAttribute(attributeType, convertSet(delegate.getTemagrupperForAnsattesValgteEnhet(getSubjectId(context)))));
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
