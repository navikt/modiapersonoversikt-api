package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.interfaces.RequestContext;

import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;

public class KontorsperrePolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessSammeLokalEnhet() {
        RequestContext request = createRequest(ENHET, ENHET);
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessIkkeSammeLokalEnhet() {
        RequestContext request = createRequest(ENHET, "1234");
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void allowAccessSammeLokalEnheter() {
        RequestContext request = createRequest(ENHET, ENHET, ENHET2);
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
        RequestContext request2 = createRequest(ENHET2, ENHET, ENHET2);
        assertThat(pdp.evaluate(request2)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessUlikLokalEnheter() {
        RequestContext request = createRequest(ENHET, "0314", ENHET2);
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void denyAccessHvisSubjectEnhetIkkeErSatt() {
        RequestContext request = createRequest(ENHET, null);
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void allowAccessHvisAnsvarligEnhetIkkeErSatt() {//Melding ikke kontorsperret
        RequestContext request = createRequest(null, "ENHET");
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    private RequestContext createRequest(String ansvarligEnhet, String... saksbehandlerEnheter) {
        XACMLRequestBuilder req = XACMLRequestBuilder.create();

        req.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID);
        req.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR);
        req.withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre");
        if (saksbehandlerEnheter != null && saksbehandlerEnheter.length > 0) {
            for (String saksbehandlerEnhet : saksbehandlerEnheter) {
                req.withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, saksbehandlerEnhet);
            }
        }
        if (ansvarligEnhet != null && !ansvarligEnhet.isEmpty()) {
            req.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ansvarligEnhet);
        }

        return req.build();
    }

}
