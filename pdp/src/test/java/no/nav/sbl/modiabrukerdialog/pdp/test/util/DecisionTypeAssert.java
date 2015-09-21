package no.nav.sbl.modiabrukerdialog.pdp.test.util;

import org.assertj.core.api.AbstractAssert;
import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.interfaces.ResponseContext;

import java.util.Objects;

public class DecisionTypeAssert extends AbstractAssert<DecisionTypeAssert, ResponseContext> {

    public DecisionTypeAssert(ResponseContext actual) {
        super(actual, DecisionTypeAssert.class);
    }

    public static DecisionTypeAssert assertThat(ResponseContext actual) {
        return new DecisionTypeAssert(actual);
    }

    public DecisionTypeAssert hasDecision(DecisionType decisionType) {
        isNotNull();

        if (!Objects.equals(actual.getResult().getDecision(), decisionType)) {
            failWithMessage("Expected decision to be <%s>, but was <%s>", actual.getResult().getDecision(), decisionType);
        }

        return this;
    }


}