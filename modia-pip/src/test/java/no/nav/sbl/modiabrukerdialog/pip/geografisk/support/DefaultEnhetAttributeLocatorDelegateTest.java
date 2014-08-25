package no.nav.sbl.modiabrukerdialog.pip.geografisk.support;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.config.GeografiskPipConfig;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.ASBOGOSYSHentNAVEnhetListeRequest;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.ASBOGOSYSNAVAnsatt;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.ASBOGOSYSNAVEnhetListe;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.ASBOGOSYSNavEnhet;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.GOSYSNAVOrgEnhet;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.GOSYSNAVansatt;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class DefaultEnhetAttributeLocatorDelegateTest {

	private static final String ANSATT_ID = "Z900001";
	private static final String LOKAL_ENHET_ID = "1222";
	private static final String FYLKES_ENHET_ID = "1111";
	private static final String ENHET_ID_I_SAMME_FYLE = "1333";
	private EnhetAttributeLocatorDelegate delegate;
	@Mock
	private GOSYSNAVansatt ansattService;
	@Mock
	private GOSYSNAVOrgEnhet enhetService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		delegate = new DefaultEnhetAttributeLocatorDelegate(ansattService, enhetService);
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
		when(enhetService.hentNAVEnhetGruppeListe(any(ASBOGOSYSNavEnhet.class))).thenReturn(getEnhetList(ENHET_ID_I_SAMME_FYLE));

		Set<String> values = delegate.getFylkesenheterForAnsatt(ANSATT_ID);
		assertThat(values.size(), is(3));
	}

	@Test
	public void getFylkesenheterForAnsattMedSpesialEnhet() throws Exception {
		ASBOGOSYSNAVEnhetListe enhetListe = getEnhetList(LOKAL_ENHET_ID);
		enhetListe.getNAVEnheter().get(0).setOrgNivaKode("SPESEN");
		when(ansattService.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(enhetListe);
		when(enhetService.hentNAVEnhetGruppeListe(any(ASBOGOSYSNavEnhet.class))).thenReturn(getEnhetList(ENHET_ID_I_SAMME_FYLE));

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
