package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;

public class OkonomiskSosialhjelpPolicyTest extends AbstractPDPTest {

    @Test
    public void allowAccessSammeLokalEnhet() {
        RequestContext request = createRequest("9999", "9999");
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessIkkeSammeLokalEnhet() {
        RequestContext request = createRequest("1234", "5678");
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void denyAccessHvisSubjectEnhetIkkeErSatt() {
        RequestContext request = createRequest(null, "9999");
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void allowAccessHvisRiktigRolle() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-Okonomisk_Sosialhjelp")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void denyAccessHvisIkkeRiktigRolle() {
        RequestContext request = XACMLRequestBuilder.create()
                .withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_REGIONAL")
                .withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
                .withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    private RequestContext createRequest(String saksbehandlerEnhet, String brukersEnhet) {
        XACMLRequestBuilder req = XACMLRequestBuilder.create();

        req.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID);
        req.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR);
        req.withActionAttr(ATTRIBUTEID_ACTION_ID, "oksos");

        if (saksbehandlerEnhet != null && !saksbehandlerEnhet.isEmpty()) {
            req.withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, saksbehandlerEnhet);
        }
        if (brukersEnhet != null && !brukersEnhet.isEmpty()) {
            req.withResourceAttr(ATTRIBUTEID_BRUKER_ENHET, brukersEnhet);
        }

        return req.build();
    }

}
