package no.nav.sbl.modiabrukerdialog.pip.journalforing.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.ASBOGOSYSFagomrade;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTemagruppeAttributeLocatorDelegateTest {

    @Mock
    private GOSYSNAVOrgEnhet gosysnavOrgEnhet;

    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private DefaultTemagruppeAttributeLocatorDelegate delegate;

    @Before
    public void setUp() {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("0000");
    }

    @Test
    public void henterTemagrupperBasertPaaSaksbehandlersEnhetsValg() throws HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg, HentNAVEnhetFaultGOSYSGeneriskMsg {
        ASBOGOSYSNavEnhet enhet = new ASBOGOSYSNavEnhet();
        enhet.setEnhetsId("1111");

        ASBOGOSYSFagomrade fagomrade1 = new ASBOGOSYSFagomrade();
        fagomrade1.setFagomradeKode("ARBD");
        ASBOGOSYSFagomrade fagomrade2 = new ASBOGOSYSFagomrade();
        fagomrade2.setFagomradeKode("FAML");

        setInternalState(enhet, "fagomrader", asList(fagomrade1, fagomrade2));

        when(gosysnavOrgEnhet.hentNAVEnhet(any(ASBOGOSYSNavEnhet.class))).thenReturn(enhet);

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet(), contains("ARBD", "FAML"));
        verify(saksbehandlerInnstillingerService, only()).getSaksbehandlerValgtEnhet();
        verify(gosysnavOrgEnhet, only()).hentNAVEnhet(any(ASBOGOSYSNavEnhet.class));
    }

    @Test
    public void emptySetHvisNorgIkkeFinnerEnhet() throws HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg, HentNAVEnhetFaultGOSYSGeneriskMsg {
        when(gosysnavOrgEnhet.hentNAVEnhet(any(ASBOGOSYSNavEnhet.class))).thenThrow(new HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg());

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet(), is(empty()));
    }

    @Test
    public void emptySetHvisNorgFeiler() throws HentNAVEnhetFaultGOSYSNAVEnhetIkkeFunnetaMsg, HentNAVEnhetFaultGOSYSGeneriskMsg {
        when(gosysnavOrgEnhet.hentNAVEnhet(any(ASBOGOSYSNavEnhet.class))).thenThrow(new HentNAVEnhetFaultGOSYSGeneriskMsg());

        assertThat(delegate.getTemagrupperForAnsattesValgteEnhet(), is(empty()));
    }
}