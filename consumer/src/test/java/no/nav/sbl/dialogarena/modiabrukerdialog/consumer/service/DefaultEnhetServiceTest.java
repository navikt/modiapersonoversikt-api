package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSFinnNAVEnhetRequest;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.FinnNAVEnhetFaultGOSYSGeneriskMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEnhetServiceTest {

    @Mock
    private GOSYSNAVOrgEnhet enhetWS;

    @InjectMocks
    private DefaultEnhetService defaultEnhetService;

    @Test
    public void skalHenteAlleTyperEnheter() throws FinnNAVEnhetFaultGOSYSGeneriskMsg {
        ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
        ASBOGOSYSNavEnhet navEnhet = new ASBOGOSYSNavEnhet();
        navEnhet.setEnhetsId("1111");
        navEnhet.setEnhetsNavn("Enhet");
        enhetListe.getNAVEnheter().add(navEnhet);
        when(enhetWS.finnNAVEnhet(any(ASBOGOSYSFinnNAVEnhetRequest.class))).thenReturn(enhetListe);

        List<AnsattEnhet> enheter = defaultEnhetService.hentAlleEnheter();

        verify(enhetWS, times(5)).finnNAVEnhet(any(ASBOGOSYSFinnNAVEnhetRequest.class));
        assertThat(enheter.size(), is(5));
    }

    @Test
    public void skalHenteAlleMuligeTyperEnheterSelvOmEtKallFeiler() throws FinnNAVEnhetFaultGOSYSGeneriskMsg {
        ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
        ASBOGOSYSNavEnhet navEnhet = new ASBOGOSYSNavEnhet();
        navEnhet.setEnhetsId("1111");
        navEnhet.setEnhetsNavn("Enhet");
        enhetListe.getNAVEnheter().add(navEnhet);
        when(enhetWS.finnNAVEnhet(any(ASBOGOSYSFinnNAVEnhetRequest.class))).thenThrow(new FinnNAVEnhetFaultGOSYSGeneriskMsg()).thenReturn(enhetListe);

        List<AnsattEnhet> enheter = defaultEnhetService.hentAlleEnheter();

        verify(enhetWS, times(5)).finnNAVEnhet(any(ASBOGOSYSFinnNAVEnhetRequest.class));
        assertThat(enheter.size(), is(4));
    }

    @Test
    public void skalSortereEnheterIStigendeRekkefolge() throws FinnNAVEnhetFaultGOSYSGeneriskMsg {
        ASBOGOSYSNAVEnhetListe enhetListe = new ASBOGOSYSNAVEnhetListe();
        ASBOGOSYSNavEnhet navEnhet1 = new ASBOGOSYSNavEnhet();
        navEnhet1.setEnhetsId("1111");
        navEnhet1.setEnhetsNavn("Enhet");
        ASBOGOSYSNavEnhet navEnhet2 = new ASBOGOSYSNavEnhet();
        navEnhet2.setEnhetsId("2222");
        navEnhet2.setEnhetsNavn("Enhet");
        ASBOGOSYSNavEnhet navEnhet3 = new ASBOGOSYSNavEnhet();
        navEnhet3.setEnhetsId("3333");
        navEnhet3.setEnhetsNavn("Enhet");
        enhetListe.getNAVEnheter().addAll(asList(navEnhet3, navEnhet2, navEnhet1));
        when(enhetWS.finnNAVEnhet(any(ASBOGOSYSFinnNAVEnhetRequest.class))).thenReturn(enhetListe).thenReturn(new ASBOGOSYSNAVEnhetListe());

        List<AnsattEnhet> enheter = defaultEnhetService.hentAlleEnheter();

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }
}