package no.nav.sbl.modiabrukerdialog.pip.diskresjon;

import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class DiskresjonAttributeLocator extends AttributeLocator {

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context, int designatorType) {

        //legger til tom atributt for å ikke spamme log
        Set<AttributeValue> values = new HashSet<>();
        values.add(new StringAttribute(""));

        return new EvaluationResult(new BagAttribute(attributeType, values));
    }
}
