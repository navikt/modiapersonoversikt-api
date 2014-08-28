package no.nav.sbl.modiabrukerdialog.pdp;


import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_RESOURCE_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;
import static org.junit.Assert.assertEquals;

public class AdressePolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessAdresserPanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-BD06_EndreKontaktAdresse")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "adresser")
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "update")
				.build();
		assertEquals("Access should be permitted.", DecisionType.PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void denyAccessAdresserPanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "NOT 0000-GA-BD06_EndreKontaktAdresse")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "adresser")
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "update")
				.build();
		assertEquals("Access should be denied.", DecisionType.DENY, pdp.evaluate(request).getResult().getDecision());
	}
}
