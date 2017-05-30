package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;

public class DiskresjonPolicyTest extends AbstractPDPTest {

    public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
    public static final String ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";

    @Test
	public void permitIfNoDiscretionCode() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "0")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}

	@Test
	public void denyIfInternBrukerNotKode7AndRessursKode7() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "SPFO")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(DENY);
	}

	@Test
	public void allowIfInternBrukerKode7AndRessursKode7() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE7")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "SPFO")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}

	@Test
	public void denyIfInternBrukerNotKode6AndRessursKode6() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "SPSF")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(DENY);
	}

	@Test
	public void allowIfInternBrukerKode6AndRessursKode6() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE6")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "SPSF")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}


    @Test
    public void allowLesFamilierelasjonSPSFIfRessursKode6() throws Exception {
        RequestContext request = createRequestBuilder()
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE6")
                .withResourceAttr(RESOURCE_ID, "personMedDiskresjonkode")
                .withActionAttr(ACTION_ID, "lesKodeSPSF")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void doNotAllowLesFamilieSPSFIfRessursKode7() throws Exception {
        RequestContext request = createRequestBuilder()
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE7")
                .withResourceAttr(RESOURCE_ID, "personMedDiskresjonkode")
                .withActionAttr(ACTION_ID, "lesKodeSPSF")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }

    @Test
    public void allowLesFamilierelasjonSPFOIfRessursKode7() throws Exception {
        RequestContext request = createRequestBuilder()
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE7")
                .withResourceAttr(RESOURCE_ID, "personMedDiskresjonkode")
                .withActionAttr(ACTION_ID, "lesKodeSPFO")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
    }

    @Test
    public void doNotAllowLesFamilieSPFOIfRessursKode6() throws Exception {
        RequestContext request = createRequestBuilder()
                .withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE6")
                .withResourceAttr(RESOURCE_ID, "personMedDiskresjonkode")
                .withActionAttr(ACTION_ID, "lesKodeSPFO")
                .build();
        assertThat(pdp.evaluate(request)).hasDecision(DENY);
    }
}

