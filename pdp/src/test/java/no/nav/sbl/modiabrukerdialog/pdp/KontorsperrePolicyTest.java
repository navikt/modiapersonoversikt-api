package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;
import static org.junit.Assert.assertEquals;


public class KontorsperrePolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessSammeLokalEnhet() {
        RequestContext request = createRequest(ENHET, ENHET);
        assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessIkkeSammeLokalEnhet() {
        RequestContext request = createRequest("1234", ENHET);
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessHvisSubjectEnhetIkkeErSatt() {
        RequestContext request = createRequest(null, ENHET);
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void allowAccessHvisAnsvarligEnhetIkkeErSatt() {//Melding ikke kontorsperret
        RequestContext request = createRequest("ENHET", null);
        assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    private RequestContext createRequest(String saksbehandlerEnhet, String ansvarligEnhet) {
        XACMLRequestBuilder req = XACMLRequestBuilder.create();

        req.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID);
        req.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR);
        req.withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre");

        if (saksbehandlerEnhet != null && !saksbehandlerEnhet.isEmpty()) {
            req.withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, saksbehandlerEnhet);
        }
        if (ansvarligEnhet != null && !ansvarligEnhet.isEmpty()) {
            req.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ansvarligEnhet);
        }

        return req.build();
    }
}
