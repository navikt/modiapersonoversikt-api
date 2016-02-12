package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.sak.service.interfaces.BulletproofCmsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.sak.service.interfaces.BulletProofKodeverkService.BEHANDLINGSTEMA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BulletProofKodeverkServiceImplTest {

    public static final String SKJEMANUMMER_DAGPENGER = "NAV 04-01.03";
    public static final String TEMANAVN_DAGPENGER = "Dagpenger";
    public static final String TEMAKODE_DAGPENGER = "DAG";

    public static final String EXCEPTION_TEMA = "UFO";
    public static final String SKJEMANUMMER_UFORE = "NAV 12-06.06";
    private static final String TITTEL = "tittel";
    private static final String SOK_TITTEL = "sok_tittel";
    private static final String EXCEPTION_TITTEL = "exc_tittel";

    @InjectMocks
    private BulletProofKodeverkServiceImpl kodeverkWrapper = new BulletProofKodeverkServiceImpl();

    @Mock
    private Kodeverk lokaltKodeverk;

    @Mock
    private KodeverkClient kodeverkClient;

    @Mock
    private BulletproofCmsService bulletproofCmsService;

    @Before
    public void setupMocks() {
        when(lokaltKodeverk.getKode(SKJEMANUMMER_DAGPENGER, Kodeverk.Nokkel.TEMA)).thenReturn(TEMAKODE_DAGPENGER);
        when(lokaltKodeverk.getKode(SKJEMANUMMER_UFORE, Kodeverk.Nokkel.TEMA)).thenThrow(new RuntimeException());

        when(bulletproofCmsService.hentTekst(anyString())).thenReturn("ledetekst");
    }

    @Test
    public void getTittel_shouldReturn_tittel() {
        when(lokaltKodeverk.getTittel(SOK_TITTEL)).thenReturn(TITTEL);
        String tittel = kodeverkWrapper.getSkjematittelForSkjemanummer(SOK_TITTEL);
        assertThat(tittel, is(TITTEL));
    }

    @Test
    public void getTittel_shouldNot_throwExceptionWhenErrorOccurs() {
        when(lokaltKodeverk.getTittel(EXCEPTION_TITTEL)).thenThrow(new RuntimeException());
        kodeverkWrapper.getSkjematittelForSkjemanummer(EXCEPTION_TITTEL);
    }

    @Test
    public void getTemaForTemakode_shouldReturn_tema() {
        when(kodeverkClient.hentFoersteTermnavnForKode(TEMAKODE_DAGPENGER, BEHANDLINGSTEMA)).thenReturn(TEMANAVN_DAGPENGER);
        String tema = kodeverkWrapper.getTemanavnForTemakode(TEMAKODE_DAGPENGER, BEHANDLINGSTEMA);
        assertThat(tema, is(TEMANAVN_DAGPENGER));
    }

    @Test
    public void getTemanavnForTemaKode_shouldNot_throwExceptionWhenErrorOccurs() {
        when(kodeverkClient.hentFoersteTermnavnForKode(EXCEPTION_TEMA, BEHANDLINGSTEMA)).thenThrow(new RuntimeException());
        kodeverkWrapper.getTemanavnForTemakode(EXCEPTION_TEMA, BEHANDLINGSTEMA);
    }


}
