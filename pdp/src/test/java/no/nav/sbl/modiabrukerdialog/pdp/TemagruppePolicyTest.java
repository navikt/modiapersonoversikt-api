package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator.ATTRIBUTEID_TEMA;
import static org.jboss.security.xacml.core.model.context.DecisionType.*;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;

public class TemagruppePolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessForTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "ARBD")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "ARBD")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessForFeilTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "ARBD")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "FAML")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void denyAccessForIngenTemagruppe() {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "temagruppe")
                .withSubjectAttr(ATTRIBUTEID_TEMA.toString(), "")
                .withResourceAttr("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", "FAML")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }
}
