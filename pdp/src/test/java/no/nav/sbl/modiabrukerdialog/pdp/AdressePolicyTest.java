package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.jupiter.api.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;

public class AdressePolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessAdresserPanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-BD06_EndreKontaktAdresse")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "adresser")
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "update")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}

	@Test
	public void denyAccessAdresserPanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "NOT 0000-GA-BD06_EndreKontaktAdresse")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "adresser")
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "update")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(DENY);
	}
}
