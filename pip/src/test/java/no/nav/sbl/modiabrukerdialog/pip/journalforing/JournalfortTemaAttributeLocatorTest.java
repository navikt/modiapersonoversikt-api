package no.nav.sbl.modiabrukerdialog.pip.journalforing;

import no.nav.sbl.modiabrukerdialog.pip.journalforing.support.JournalfortTemaAttributeLocatorDelegate;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static no.nav.sbl.dialogarena.common.collections.Collections.asSet;
import static no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator.ATTRIBUTEID_TEMA;
import static no.nav.sbl.modiabrukerdialog.pip.journalforing.JournalfortTemaAttributeLocator.STRING_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JournalfortTemaAttributeLocatorTest {

    @Mock
    private JournalfortTemaAttributeLocatorDelegate delegate;

    @Mock
    private EvaluationCtx context;

    @InjectMocks
    private JournalfortTemaAttributeLocator journalfortTemaAttributeLocator;

    @Before
    public void setUp() {
        journalfortTemaAttributeLocator.getSupportedIds().add(ATTRIBUTEID_TEMA);
        when(context.getSubjectAttribute(any(URI.class), any(URI.class), any(URI.class))).thenReturn(new EvaluationResult(JBossXACMLUtil.getAttributeValue("Z900001")));
    }

    @Test
    public void bagMedTemagrupperVedVellykketKallTilDelegate() {
        when(delegate.getTemagrupperForAnsattesValgteEnhet(anyString())).thenReturn(asSet("ARBD", "FAML"));
        EvaluationResult evaluationResult =
                journalfortTemaAttributeLocator.findAttribute(STRING_TYPE, ATTRIBUTEID_TEMA, null, null, context, 0);

        assertThat(((BagAttribute) evaluationResult.getAttributeValue()).size(), is(2));
        verify(delegate, only()).getTemagrupperForAnsattesValgteEnhet(anyString());
    }

    @Test
    public void tomBagVedUgyldigAttributeId() {
        URI ugyldigAttribute = URI.create("UGYLDIG_ATTRIBUTE");
        EvaluationResult evaluationResult =
                journalfortTemaAttributeLocator.findAttribute(null, ugyldigAttribute, null, null, context, 0);

        assertTrue(((BagAttribute) evaluationResult.getAttributeValue()).isEmpty());
        assertThat(evaluationResult.getAttributeValue().getType(), is(ugyldigAttribute));
        verifyZeroInteractions(delegate);
    }

    @Test
    public void tomBagVedUgyldigAttributeIdOgOppgittAttributeType() {
        EvaluationResult evaluationResult =
                journalfortTemaAttributeLocator.findAttribute(STRING_TYPE, URI.create("UGYLDIG_ATTRIBUTE"), null, null, context, 0);

        assertTrue(((BagAttribute) evaluationResult.getAttributeValue()).isEmpty());
        assertThat(evaluationResult.getAttributeValue().getType(), is(STRING_TYPE));
        verifyZeroInteractions(delegate);
    }
}