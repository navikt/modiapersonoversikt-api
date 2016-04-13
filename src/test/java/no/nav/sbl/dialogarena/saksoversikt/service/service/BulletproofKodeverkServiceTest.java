package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.BEHANDLINGSTEMA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulletproofKodeverkServiceTest {

    public static final String SKJEMANUMMER_DAGPENGER = "NAV 04-01.03";
    public static final String TEMANAVN_DAGPENGER = "Dagpenger";
    public static final String TEMAKODE_DAGPENGER = "DAG";

    public static final String TEMAKODE_UFORE = "UFO";
    public static final String SKJEMANUMMER_UFORE = "NAV 12-06.06";
    private static final String TITTEL = "tittel";
    private static final String SOK_TITTEL = "sok_tittel";
    private static final String EXCEPTION_TITTEL = "exc_tittel";

    @InjectMocks
    private BulletproofKodeverkService kodeverkWrapper = new BulletproofKodeverkService();

    @Mock
    private Kodeverk lokaltKodeverk;

    @Mock
    private KodeverkClient kodeverkClient;

    @Mock
    private CmsContentRetriever cmsContentRetriever;

    @Before
    public void setupMocks() {
        when(lokaltKodeverk.getKode(SKJEMANUMMER_DAGPENGER, Kodeverk.Nokkel.TEMA)).thenReturn(TEMAKODE_DAGPENGER);
        when(lokaltKodeverk.getKode(SKJEMANUMMER_UFORE, Kodeverk.Nokkel.TEMA)).thenThrow(new RuntimeException());
        when(lokaltKodeverk.getKode(SOK_TITTEL, Kodeverk.Nokkel.TITTEL)).thenReturn(TITTEL);
        when(lokaltKodeverk.getKode(EXCEPTION_TITTEL, Kodeverk.Nokkel.TITTEL)).thenThrow(new RuntimeException());
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMAKODE_DAGPENGER, BEHANDLINGSTEMA)).thenReturn(TEMANAVN_DAGPENGER);
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMAKODE_UFORE, BEHANDLINGSTEMA)).thenThrow(new RuntimeException());

        when(cmsContentRetriever.hentTekst(anyString())).thenReturn("ledetekst");
    }

    @Test
    public void getTittel_shouldReturn_tittel() {
        String tittel = kodeverkWrapper.getSkjematittelForSkjemanummer(SOK_TITTEL);
        assertThat(tittel, is(TITTEL));
    }

    @Test
    public void getTittel_shouldNot_throwExceptionWhenErrorOccurs() {
        kodeverkWrapper.getSkjematittelForSkjemanummer(EXCEPTION_TITTEL);
    }

    @Test
    public void getTemaForTemakode_shouldReturn_tema() {
        ResultatWrapper tema = kodeverkWrapper.getTemanavnForTemakode(TEMAKODE_DAGPENGER, BEHANDLINGSTEMA);
        assertThat(tema.resultat, is(TEMANAVN_DAGPENGER));
    }

    @Test
    public void getTemanavnForTemaKode_shouldThrowFeilendeBaksystemExceptionWhenRuntimeErrorOccours() {
        ResultatWrapper temanavnForTemakode = kodeverkWrapper.getTemanavnForTemakode(TEMAKODE_UFORE, BEHANDLINGSTEMA);
        assertTrue(temanavnForTemakode.feilendeSystemer.contains(Baksystem.KODEVERK));
    }


}
