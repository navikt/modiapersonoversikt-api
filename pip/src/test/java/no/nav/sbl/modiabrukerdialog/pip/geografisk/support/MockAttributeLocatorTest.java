package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.EnhetAttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class MockAttributeLocatorTest {

	private MockAttributeLocator mockAttributeLocator;
	private static final String ANSATT_ID = "Z900001";
	private static final String ANSATT_ID2 = "Z900002";
	private static final String ANSATT_ID3 = "Z900003";

	@Mock
	private EvaluationCtx context;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockAttributeLocator = new MockAttributeLocator();
	}

	@Test
	public void testFindAttributeNotSupported() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, new URI("urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code"), null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	@Test
	public void testFindAttributeIsNull() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, null, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	@Test
	public void testFindAttributeEmpty() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	@Test
	public void testFindAttribute() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID2)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, EnhetAttributeLocator.ATTRIBUTEID_LOCAL_ENHET, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(!((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	@Test
	public void testFindAttributeFylkesenhet() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID3)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, EnhetAttributeLocator.ATTRIBUTEID_FYLKESENHET, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(!((BagAttribute) result.getAttributeValue()).isEmpty());
	}

	@Test
	public void testFindAttributeRoller() throws Exception {
		when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue(ANSATT_ID)));
		EvaluationResult result = mockAttributeLocator.findAttribute(EnhetAttributeLocator.STRING_TYPE, EnhetAttributeLocator.ATTRIBUTEID_ROLLE, null, EnhetAttributeLocator.SUBJECT_CATEGORY, context, 0);
		assertTrue(!((BagAttribute) result.getAttributeValue()).isEmpty());
	}
}
