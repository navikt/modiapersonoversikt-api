package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.*;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_RESOURCE_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;

public class PlukkOppgavePolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessPlukkOppgavePanel() throws Exception {
        RequestContext request = createRequestBuilder()
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-BD06_HentOppgave")
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "plukkoppgave")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessPlukkOppgavePanel() throws Exception {
        RequestContext request = createRequestBuilder()
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "plukkoppgave")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }
}
