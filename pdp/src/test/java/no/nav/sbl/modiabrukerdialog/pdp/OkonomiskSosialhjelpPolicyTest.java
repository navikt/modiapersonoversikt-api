package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;
import static org.junit.Assert.assertEquals;

public class OkonomiskSosialhjelpPolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessSammeLokalEnhet() {
        RequestContext request = createRequest("9999", "9999");
        assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessIkkeSammeLokalEnhet() {
        RequestContext request = createRequest("1234", "5678");
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessHvisSubjectEnhetIkkeErSatt() {
        RequestContext request = createRequest(null, "9999");
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void allowAccessHvisRiktigRolle() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-Okonomisk_Sosialhjelp")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos")
                .build();
        assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
    }

    @Test
    public void denyAccessHvisIkkeRiktigRolle() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_REGIONAL")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos")
                .build();
        assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
    }

    private RequestContext createRequest(String saksbehandlerEnhet, String tilknyttetEnhet) {
        XACMLRequestBuilder req = XACMLRequestBuilder.create();

        req.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID);
        req.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR);
        req.withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos");

        if (saksbehandlerEnhet != null && !saksbehandlerEnhet.isEmpty()) {
            req.withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, saksbehandlerEnhet);
        }
        if (tilknyttetEnhet != null && !tilknyttetEnhet.isEmpty()) {
            req.withResourceAttr(ATTRIBUTEID_TILKNYTTET_ENHET, tilknyttetEnhet);
        }

        return req.build();
    }

}
