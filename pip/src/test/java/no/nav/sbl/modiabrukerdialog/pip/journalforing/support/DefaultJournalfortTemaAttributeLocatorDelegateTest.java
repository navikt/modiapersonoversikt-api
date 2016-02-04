package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomradeListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSHentNAVAnsattFagomradeListeRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class DefaultJournalfortTemaAttributeLocatorDelegateTest {

    @Mock
    private GOSYSNAVansatt ansattService;

    @InjectMocks
    private DefaultJournalfortTemaAttributeLocatorDelegate delegate;

    @Test
    public void henterTemaerBasertPaaSaksbehandlersEnhetsValg() throws HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg, HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        ASBOGOSYSFagomradeListe fagomradeListe = new ASBOGOSYSFagomradeListe();

        ASBOGOSYSFagomrade fagomrade1 = new ASBOGOSYSFagomrade();
        fagomrade1.setFagomradeKode("ARBD");
        ASBOGOSYSFagomrade fagomrade2 = new ASBOGOSYSFagomrade();
        fagomrade2.setFagomradeKode("FAML");

        setInternalState(fagomradeListe, "fagomrader", asList(fagomrade1, fagomrade2));

        when(ansattService.hentNAVAnsattFagomradeListe(any(ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class))).thenReturn(fagomradeListe);

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet("", ""), containsInAnyOrder("ARBD", "FAML"));
        verify(ansattService, only()).hentNAVAnsattFagomradeListe(any(ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class));
    }

    @Test
    public void emptySetHvisNorgIkkeFinnerEnhet() throws HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg, HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(ansattService.hentNAVAnsattFagomradeListe(any(ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class))).thenThrow(new HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg());

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet("", ""), is(empty()));
    }

    @Test
    public void emptySetHvisNorgFeiler() throws HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg, HentNAVAnsattFagomradeListeFaultGOSYSNAVAnsattIkkeFunnetMsg {
        when(ansattService.hentNAVAnsattFagomradeListe(any(ASBOGOSYSHentNAVAnsattFagomradeListeRequest.class))).thenThrow(new HentNAVAnsattFagomradeListeFaultGOSYSGeneriskMsg());

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet("", ""), is(empty()));
    }
}