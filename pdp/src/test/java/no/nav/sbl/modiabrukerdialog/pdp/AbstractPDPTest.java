package no.nav.sbl.modiabrukerdialog.pdp;

import no.nav.sbl.modiabrukerdialog.pdp.test.util.XACMLRequestBuilder;
import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.jboss.security.xacml.interfaces.XACMLConstants.ATTRIBUTEID_RESOURCE_ID;

public class AbstractPDPTest {

	protected static final String ATTRIBUTEID_LOCAL_ENHET = "urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet";
	protected static final String ATTRIBUTEID_FYLKESENHET = "urn:nav:ikt:tilgangskontroll:xacml:subject:fylkesenhet";
	protected static final String ATTRIBUTEID_ANSVARLIG_ENHET = "urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet";
	protected static final String ATTR_ID_DISCRETION_CODE = "urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code";
	protected static final String POLICY_FILE = "config/modia-policy-config-test.xml";
	protected static final String ACTION_ID = "les";
	protected static final String ACTION_ID_MED_BEGRUNNELSE = "lesMedBegrunnelse";
	protected static final String FNR = "***REMOVED***";
	protected static final String ENHET = "0313";
	protected static final String SUBJECT_ID = "Z999999";
	protected static PolicyDecisionPoint pdp;

	@BeforeClass
	public static void setupOnce() {
		System.setProperty("DiskresjonskodeLocator.url", "http://www.test.no/");
		pdp = new JBossPDP(GeografiskPolicyTest.class.getClassLoader().getResourceAsStream(POLICY_FILE));
	}

	@AfterClass
	public static void cleanUp() {
		System.clearProperty("DiskresjonskodeLocator.url");
	}

	protected XACMLRequestBuilder createRequestBuilder() {
		return XACMLRequestBuilder.create().withResourceAttr(ATTRIBUTEID_RESOURCE_ID, FNR);
	}
}

