package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator.ATTRIBUTEID_TEMA;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.junit.Assert.assertEquals;

public class TemagruppePolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessForTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "ARBD")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "ARBD")
                .build();
        assertEquals("Access should be permitted.", DecisionType.PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessForFeilTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "ARBD")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "FAML")
                .build();
        assertEquals("Access should be denied.", DecisionType.DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessForIngenTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "FAML")
                .build();
        assertEquals("Access should be denied.", DecisionType.DENY, pdp.evaluate(request).getResult().getDecision());
    }
}
