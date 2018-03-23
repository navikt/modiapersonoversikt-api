package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.jupiter.api.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.*;

public class AaregPolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessPlukkOppgavePanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-Aa-register-Lese")
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "aaregles")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}

	@Test
	public void denyAccessPlukkOppgavePanel() throws Exception {
		RequestContext request = createRequestBuilder()
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "aaregles")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(DENY);
	}
}
