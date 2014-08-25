package no.nav.sbl.modiabrukerdialog.pip.geografisk;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.config.GeografiskPipConfig;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class EnhetAttributeLocatorTest {

	private static final String ANSATT_ID = "Z900001";
	private static final String LOKAL_ENHET_ID = "1222";
	private EnhetAttributeLocator locator;
	@Mock
	private EvaluationCtx context;
	@Mock
	private EnhetAttributeLocatorDelegate mockDelegate;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		locator = new EnhetAttributeLocator();
		locator.getSupportedIds().add(EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET);
		locator.getSupportedIds().add(EnhetAttributeLocator.ATTRIBUTEID_FYLKESENHET);

		//Normally, initialized within the class.
		Whitebox.setInternalState(locator, "delegate", mockDelegate);

		when(mockDelegate.getLokalEnheterForAnsatt(anyString())).thenReturn(new HashSet<>(Arrays.asList(LOKAL_ENHET_ID)));
		when(mockDelegate.getFylkesenheterForAnsatt(anyString())).thenReturn(new HashSet<>(Arrays.asList(LOKAL_ENHET_ID)));
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID)));
	}

	@BeforeClass
	public static void setUpOnce() {
		System.setProperty(GeografiskPipConfig.TJENESTEBUSS_URL_KEY, "https://tjenestebuss-t6.adeo.no/");
		System.setProperty(GeografiskPipConfig.TJENESTEBUSS_USERNAME_KEY, "srvGosys");
		System.setProperty(GeografiskPipConfig.TJENESTEBUSS_PASSWORD_KEY, "***");
	}

	@AfterClass
	public static void cleanUp() {
		System.clearProperty(GeografiskPipConfig.TJENESTEBUSS_URL_KEY);
		System.clearProperty(GeografiskPipConfig.TJENESTEBUSS_USERNAME_KEY);
		System.clearProperty(GeografiskPipConfig.TJENESTEBUSS_PASSWORD_KEY);
	}

	@Test
	public void testFindLocalEnhetAttribute() {
		EvaluationResult result = findAttribute(EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET);
		assertEquals(1, ((BagAttribute) result.getAttributeValue()).size());
	}

	@Test
	public void testFindFylkesenhetAttribute() {
		EvaluationResult result = findAttribute(EnhetAttributeLocator.ATTRIBUTEID_FYLKESENHET);
		assertEquals(1, ((BagAttribute) result.getAttributeValue()).size());
	}

	@Test
	public void testFindNonSupportedAttribute() {
		EvaluationResult result = findAttribute(EnhetAttributeLocator.SUBJECT_ID);
		assertTrue(((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	private EvaluationResult findAttribute(URI attributeId) {
		return locator.findAttribute(EnhetAttributeLocator.STRING_TYPE, attributeId, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
	}
}