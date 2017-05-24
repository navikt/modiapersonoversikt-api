package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.*;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2.OrganisasjonEnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.config.GeografiskPipConfig;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEnhetAttributeLocatorDelegateTest {

    private static final String ANSATT_ID = "Z900001";
    private static final String LOKAL_ENHET_ID = "1222";
    private static final String FYLKES_ENHET_ID = "1111";
    private static final String ENHET_ID_I_SAMME_FYLKE = "1333";
    private static final String ENHET_ID_I_ANNET_FYLKE = "2525";
    private static final String VALGT_ENHET = "1333";

    @Mock
    private GOSYSNAVansatt ansattService;
    @Mock
    private GOSYSNAVOrgEnhet enhetService;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private OrganisasjonEnhetService orgEnhetservice;

    @InjectMocks
    private EnhetAttributeLocatorDelegate delegate = new DefaultEnhetAttributeLocatorDelegate();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
    public void getFylkesenheterForReelAnsatt() throws Exception {
        when(ansattService.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(getEnhetList(LOKAL_ENHET_ID));
        when(enhetService.hentNAVEnhetListe(any(ASBOGOSYSHentNAVEnhetListeRequest.class))).thenReturn(getEnhetList(FYLKES_ENHET_ID));
        when(enhetService.hentNAVEnhetGruppeListe(any(ASBOGOSYSNavEnhet.class))).thenReturn(getEnhetList(ENHET_ID_I_SAMME_FYLKE));

        Set<String> values = delegate.getFylkesenheterForAnsatt(ANSATT_ID);
        assertThat(values.size(), is(3));
    }

    @Test
    public void getFylkesenheterForAnsattMedSpesialEnhet() throws Exception {
        ASBOGOSYSNAVEnhetListe enhetListe = getEnhetList(LOKAL_ENHET_ID);
        enhetListe.getNAVEnheter().get(0).setOrgNivaKode("SPESEN");
        when(ansattService.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(enhetListe);
        when(enhetService.hentNAVEnhetGruppeListe(any(ASBOGOSYSNavEnhet.class))).thenReturn(getEnhetList(ENHET_ID_I_SAMME_FYLKE));

        Set<String> values = delegate.getFylkesenheterForAnsatt(ANSATT_ID);
        assertThat("Should not contain fylkesenhet", values.size(), is(2));
    }

    @Test
    public void getLokalEnheterForEksisterendeAnsatt() throws Exception {
        when(ansattService.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(getEnhetList(LOKAL_ENHET_ID));
        Set<String> values = delegate.getLokalEnheterForAnsatt(ANSATT_ID);
        assertThat(values.size(), is(1));
    }

    @Test
    public void getLokalEnheterForIkkeEksisterendeAnsatt() throws Exception {
        when(ansattService.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenThrow(new HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg());
        Set<String> values = delegate.getLokalEnheterForAnsatt(ANSATT_ID);
        assertTrue(values.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void getArbeidsfordelingForValgtEnhet() {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);
        when(orgEnhetservice.hentArbeidsfordeling(VALGT_ENHET)).thenReturn(getArbeidsfordeling(ENHET_ID_I_SAMME_FYLKE, ENHET_ID_I_ANNET_FYLKE));
        Set<Arbeidsfordeling> values = delegate.getArbeidsfordelingForEnhet(VALGT_ENHET);
        assertThat(values.size(), is(2));
    }

    private List<Arbeidsfordeling> getArbeidsfordeling(String... enheter) {
        String arkivTema = "BIL";
        return Arrays.stream(enheter)
                .map(enhetId -> new Arbeidsfordeling(enhetId, arkivTema))
                .collect(Collectors.toList());
    }

    private ASBOGOSYSNavEnhet getEnhet(String enhetId) {
        ASBOGOSYSNavEnhet enhet = new ASBOGOSYSNavEnhet();
        enhet.setEnhetsId(enhetId);
        enhet.setOrgNivaKode("FYLKE");
        return enhet;
    }

    private ASBOGOSYSNAVEnhetListe getEnhetList(String... enhetIds) {
        ASBOGOSYSNAVEnhetListe liste = new ASBOGOSYSNAVEnhetListe();
        for (String enhetId : enhetIds) {
            liste.getNAVEnheter().add(getEnhet(enhetId));
        }
        return liste;
    }
}
