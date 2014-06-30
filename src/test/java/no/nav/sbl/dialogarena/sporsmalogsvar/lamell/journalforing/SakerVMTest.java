package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
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

    private final static String TEMA_1 = "Pensjon";
    private final static String TEMA_2 = "Dagpenger";
    private final static String TEMA_3 = "Barnebidrag";

    private ArrayList<Sak> mockSaksliste;
    private MeldingService meldingService;
    private InnboksVM innboksVM;
    private TraadVM traadVM;

    @Before
    public void setUp(){
        mockSaksliste = createMockSaksliste();
        meldingService = mock(MeldingService.class);
        innboksVM = mock(InnboksVM.class);
        traadVM = mock(TraadVM.class);
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(mockSaksliste);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        MeldingVM meldingVM = opprettMockMeldingVM();
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
    }

    private MeldingVM opprettMockMeldingVM() {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.temagruppe = "temagruppe";
        return new MeldingVM(melding, 1);
    }

    @Test
    public void gittHverSakHarUniktTemaReturnerKorrektSakstemaliste() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", TEMA_1, "Fagsak 4", DateTime.now().minusDays(5));
        mockSaksliste.add(sak4);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(3));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(2));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
    }

    private static Sak createSak(String saksId, String tema, String fagsak, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.fagsystem = fagsak;
        sak.opprettetDato = opprettet;
        sak.tema = tema;
        return sak;
    }

    private ArrayList<Sak> createMockSaksliste(){
        return new ArrayList<>(Arrays.asList(
                createSak("111111111", TEMA_1, "Fagsak 1", DateTime.now().minusDays(1)),
                createSak("222222222", TEMA_2, "Fagsak 2", DateTime.now().minusDays(4)),
                createSak("333333333", TEMA_3, "Fagsak 1", DateTime.now().minusDays(4))));
    }

}