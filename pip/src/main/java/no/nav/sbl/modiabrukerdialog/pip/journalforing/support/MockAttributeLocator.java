package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;

import java.net.URI;

import static no.nav.sbl.dialogarena.common.collections.Collections.asSet;
import static no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator.ATTRIBUTEID_TEMA;

public class MockAttributeLocator extends AttributeLocator {

    public MockAttributeLocator() {
        this.attributeDesignatorSupported = true;
        this.attributeSelectorSupported = true;
        this.ids.add(ATTRIBUTEID_TEMA);

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

        return new EvaluationResult(
                new BagAttribute(attributeType, asSet(
                        JBossXACMLUtil.getAttributeValue("ARBD"),
                        JBossXACMLUtil.getAttributeValue("FAML"))));
    }
}
