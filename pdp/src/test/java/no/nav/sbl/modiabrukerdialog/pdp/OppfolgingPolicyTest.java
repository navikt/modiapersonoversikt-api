package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.jupiter.api.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;

public class OppfolgingPolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessOppfolgingSaksbehandler() throws Exception {
		RequestContext request = createRequestBuilder()
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "oppfolging")
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-Modia-Oppfolging")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(PERMIT);
	}

	@Test
	public void denyAccessOtherSaksbehandler() throws Exception {
		RequestContext request = createRequestBuilder()
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "oppfolging")
				.withSubjectAttr(ATTRIBUTEID_ROLE, "NOT 0000-GA-Modia-Oppfolging")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(DENY);
	}

}
