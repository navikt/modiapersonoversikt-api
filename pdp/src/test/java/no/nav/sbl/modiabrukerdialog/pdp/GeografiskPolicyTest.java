package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.junit.Test;

import static org.jboss.security.xacml.core.model.context.DecisionType.DENY;
import static org.jboss.security.xacml.core.model.context.DecisionType.PERMIT;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ACTION_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_RESOURCE_ID;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_ROLE;
import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_SUBJECT_ID;
import static org.junit.Assert.assertEquals;


public class GeografiskPolicyTest extends AbstractPDPTest {

	@Test
	public void allowAccessNasjonalRolle() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_NASJONAL")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowAccessRegionalRolleSammeFylkesNivaa() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_REGIONAL")
				.withSubjectAttr(ATTRIBUTEID_FYLKESENHET, ENHET)
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void denyAccessRegionalRolleIkkeSammeFylkesNivaa() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_REGIONAL")
				.withSubjectAttr(ATTRIBUTEID_FYLKESENHET, "1234")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowAccessSammeLokalEnhet() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_LOCAL_ENHET, ENHET)
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
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
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowAccessUtvidbarNasjonalRolle() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_UTVIDBAR_TIL_NASJONAL")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowAccessToPersonWithoutAnsvarligEnhet() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, null)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void allowAccessUtvidbarTilRegionalRolleSammeFylkesNivaa() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_UTVIDBAR_TIL_REGIONAL")
				.withSubjectAttr(ATTRIBUTEID_FYLKESENHET, ENHET)
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be permitted.", PERMIT, pdp.evaluate(request).getResult().getDecision());
	}

	@Test
	public void denyAccessUtvidbarTilRegionalRolleIkkeSammeFylkesNivaa() {
		RequestContext request = XACMLRequestBuilder.create()
				.withSubjectAttr(ATTRIBUTEID_SUBJECT_ID, SUBJECT_ID)
				.withSubjectAttr(ATTRIBUTEID_ROLE, "0000-GA-GOSYS_UTVIDBAR_TIL_REGIONAL")
				.withSubjectAttr(ATTRIBUTEID_FYLKESENHET, "1234")
				.withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR)
				.withResourceAttr(ATTRIBUTEID_ANSVARLIG_ENHET, ENHET)
				.withActionAttr(ATTRIBUTEID_ACTION_ID, ACTION_ID)
				.build();
		assertEquals("Access should be denied.", DENY, pdp.evaluate(request).getResult().getDecision());
	}
}
