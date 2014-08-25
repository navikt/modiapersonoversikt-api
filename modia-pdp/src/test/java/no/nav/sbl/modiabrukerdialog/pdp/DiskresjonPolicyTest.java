package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;
import static org.junit.Assert.assertEquals;

public class DiskresjonPolicyTest extends AbstractPDPTest {

	@Test
	public void permitIfNoDiscretionCode() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "0")
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void denyIfInternBrukerNotKode7AndRessursKode7() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "7")
				.build();
		assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowIfInternBrukerKode7AndRessursKode7() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE7")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "7")
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void denyIfInternBrukerNotKode6AndRessursKode6() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE3")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "6")
				.build();
		assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowIfInternBrukerKode6AndRessursKode6() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_KODE6")
				.withResourceAttr(ATTR_ID_DISCRETION_CODE, "6")
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}
}

