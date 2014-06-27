package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Saksgruppe;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SakerVMTest {

    private final static String FAGOMRADE_1 = "Pensjon";
    private final static String FAGOMRADE_2 = "Hjelpemidler";
    private final static String FAGOMRADE_3 = "Familie og barn";

    private ArrayList<Sak> mockSaksliste;
    private MeldingService meldingService;

    @Before
    public void setUp(){
        mockSaksliste = createMockSaksliste();
        meldingService = mock(MeldingService.class);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(mockSaksliste);
    }

    @Test
    public void gittHverSakHarUniktTemaReturnerKorrektSakstemaliste() {
        SakerVM sakerVM = new SakerVM(mock(InnboksVM.class), meldingService);

        List<Saksgruppe> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", FAGOMRADE_1, "Fagsak 4", DateTime.now().minusDays(5));
        mockSaksliste.add(sak4);
        SakerVM sakerVM = new SakerVM(mock(InnboksVM.class), meldingService);

        List<Saksgruppe> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(2));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
    }

    private static Sak createSak(String saksId, String fagomrade, String fagsak, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.fagsak = fagsak;
        sak.opprettetDato = opprettet;
        sak.fagomrade = fagomrade;
        return sak;
    }

    private ArrayList<Sak> createMockSaksliste(){
        return new ArrayList<>(Arrays.asList(
                createSak("111111111", FAGOMRADE_1, "Fagsak 1", DateTime.now().minusDays(1)),
                createSak("222222222", FAGOMRADE_2, "Fagsak 2", DateTime.now().minusDays(4)),
                createSak("333333333", FAGOMRADE_3, "Fagsak 1", DateTime.now().minusDays(4))));
    }

}