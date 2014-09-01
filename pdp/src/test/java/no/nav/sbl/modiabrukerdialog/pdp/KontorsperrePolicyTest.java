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
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, ENHET)
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre")
                .build();
        assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessIkkeSammeLokalEnhet() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, "1234")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre")
                .build();
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessHvisSubjectEnhetIkkeErSatt() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre")
                .build();
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }
    @Test
    public void denyAccessHvisAnsvarligEnhetIkkeErSatt() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, "1234")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "kontorsperre")
                .build();
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }
}
