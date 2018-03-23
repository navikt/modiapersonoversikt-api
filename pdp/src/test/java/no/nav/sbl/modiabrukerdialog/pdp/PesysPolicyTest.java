package no.nav.sbl.modiabrukerdialog.pdp;

import org.jboss.security.xacml.core.model.context.DecisionType;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.jupiter.api.Test;

import static no.nav.sbl.modiabrukerdialog.pdp.test.util.DecisionTypeAssert.assertThat;
import static org.jboss.security.xacml.core.model.context.DecisionType.*;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_RESOURCE_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;

public class PesysPolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessPensjonSaksbehandler() throws Exception {
		checkDecisionForRole("0000-GA-PENSJON_SAKSBEHANDLER", PERMIT);
		checkDecisionForRole("0000-GA-Pensjon_VEILEDER", PERMIT);
		checkDecisionForRole("0000-GA-Pensjon_BEGRENSET_VEILEDER", PERMIT);
		checkDecisionForRole("0000-GA-PENSJON_BRUKERHJELPA", PERMIT);
		checkDecisionForRole("0000-GA-PENSJON_SAKSBEHANDLER", PERMIT);
		checkDecisionForRole("0000-GA-PENSJON_KLAGEBEH", PERMIT);
		checkDecisionForRole("0000-GA-Pensjon_Okonomi", PERMIT);
	}

	@Test
	public void denyAccessPensjonSaksbehandler() throws Exception {
		checkDecisionForRole("", DENY);
		checkDecisionForRole("0000-SUPER-DUMMY-ROLE", DENY);
	}

	private void checkDecisionForRole(String role, DecisionType decision) {
		RequestContext request = createRequestBuilder()
				.withSubjectAttr(ATTRIBUTEID_ROLE, role)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, "pensaksbeh")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, "")
				.build();
		assertThat(pdp.evaluate(request)).hasDecision(decision);
	}
}
