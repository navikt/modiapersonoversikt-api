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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.SakerVM.temaMapping;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SakerVMTest {

    private ArrayList<Sak> saksliste;
    private MeldingVM meldingVM;

    @Mock
    private MeldingService meldingService;

    @Mock
    private InnboksVM innboksVM;

    private List<String> alleTemaer;
    private List<String> alleTemagrupper;

    @Before
    public void setUp() {
        TraadVM traadVM = mock(TraadVM.class);
        when(innboksVM.getValgtTraad()).thenReturn(traadVM);
        alleTemaer = getAlleEksisterendeTemaer();
        alleTemagrupper = getAlleEksisterendeTemagrupper();
        saksliste = createSakslisteBasertPaTemaMap();
        when(meldingService.hentSakerForBruker(anyString())).thenReturn(saksliste);
        meldingVM = opprettMeldingVM("temagruppe");
        when(traadVM.getEldsteMelding()).thenReturn(meldingVM);
    }

    private MeldingVM opprettMeldingVM(String temagruppe) {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.temagruppe = temagruppe;
        return new MeldingVM(melding, 1);
    }

    // Gruppering på tema
    @Test
    public void gittHverSakHarUniktTemaReturnerKorrektSakstemaliste() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(4));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(1).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(2).saksliste.size(), is(1));
        assertThat(sakstemaliste.get(3).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", alleTemaer.get(0), "Fagsak 4", DateTime.now().minusDays(5));
        saksliste.add(sak4);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(4));

        for (TemaMedSaker temaMedSaker : sakstemaliste) {
            if (temaMedSaker.tema.equals(alleTemaer.get(0))) {
                assertThat(temaMedSaker.saksliste.size(), is(2));
            } else {
                assertThat(temaMedSaker.saksliste.size(), is(1));
            }
        }
    }

    //Valgt temgruppe øverst
    @Test
    public void gittTemagruppe0sjekkAtTemaMedSakerMedSammeTemagruppeSomValgtTraadLiggerForst() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.get(0).temagruppe, is(traadTemagruppe));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
    }

    @Test
    public void gittTemagruppe2sjekkAtTemaMedSakerMedSammeTemagruppeSomValgtTraadLiggerForst() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        String traadTemagruppe = alleTemagrupper.get(2);
        meldingVM.melding.temagruppe = traadTemagruppe;

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.get(0).temagruppe, is(traadTemagruppe));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
    }

    // Sorterer alfabetisk innen valgt temagruppe
    @Test
    public void gittEnTemagruppeSjekkAtTemaMedSakerMedSammeTemagruppeSomValgtTraadErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;
        List<String> traadTemagruppeSineTemaer = temaMapping.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();
        for (String tema : traadTemagruppeSineTemaer) {
            saksliste.add(createSak("44444444", tema, "Fagsystem 4", DateTime.now().minusDays(5)));
        }
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> temaInnenforValgtTemagruppe = sakerVM.getSaksgruppeliste().subList(0, traadTemagruppeLengde);

        assertSortert(temaInnenforValgtTemagruppe);
    }

    // Sorter resten av listen alfabetisk
    @Test
    public void sjekkAtDeSakeneSomIkkeHarTemagruppenTilTraadenErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;
        List<String> traadTemagruppeSineTemaer = temaMapping.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> alleSakstema = sakerVM.getSaksgruppeliste();

        List<TemaMedSaker> temaUtenforValgtTemagruppe = alleSakstema.subList(traadTemagruppeLengde, alleSakstema.size());
        assertSortert(temaUtenforValgtTemagruppe);
    }

    // Sorter etter dato innefor hvert tema?
    @Test
    public void sjekkDatoSorteringInnenforSammeTema() {
        ArrayList<Sak> sakslistekloneMedAndreDatoer = new ArrayList<>();
        for (Sak sak : saksliste) {
            sakslistekloneMedAndreDatoer.add(createSak("101010101", sak.tema, "Fagsystem", DateTime.now().minusDays(5)));
        }
        saksliste.addAll(sakslistekloneMedAndreDatoer);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        for (TemaMedSaker temaMedSaker : sakstemaliste) {
            assertDatoSortert(temaMedSaker);
        }
    }

    @Test
    public void gittTraadMedTemagruppe2MedToTilhørendeSakReturnerKorrektSakstemaliste() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        String traadTemagruppe = alleTemagrupper.get(2);
        meldingVM.melding.temagruppe = traadTemagruppe;

        List<TemaMedSaker> sakstemaliste = sakerVM.getSaksgruppeliste();

        assertThat(sakstemaliste.size(), is(4));
        assertThat(sakstemaliste.get(0).temagruppe, is(traadTemagruppe));
        assertThat(sakstemaliste.get(0).saksliste.size(), is(1));
    }

    private static Sak createSak(String saksId, String tema, String fagsak, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.fagsystem = fagsak;
        sak.opprettetDato = opprettet;
        sak.tema = tema;
        return sak;
    }

    private ArrayList<Sak> createSakslisteBasertPaTemaMap() {
        return new ArrayList<>(Arrays.asList(
                        createSak("111111111", alleTemaer.get(0), "Fagsystem1", DateTime.now().minusDays(5)),
                        createSak("22222222", alleTemaer.get(2), "Fagsystem2", DateTime.now().minusDays(3)),
                        createSak("33333333", alleTemaer.get(4), "Fagsystem3", DateTime.now().minusDays(9)),
                        createSak("44444444", alleTemaer.get(6), "Fagsystem2", DateTime.now().minusDays(2))
        ));
    }

    private ArrayList<String> getAlleEksisterendeTemaer() {
        Collection<List<String>> values = temaMapping.values();
        ArrayList<String> strings = new ArrayList<>();
        for (List<String> value : values) {
            strings.addAll(value);
        }
        return strings;
    }

    private ArrayList<String> getAlleEksisterendeTemagrupper() {
        return new ArrayList<>(temaMapping.keySet());
    }

    private void assertSortert(List<TemaMedSaker> sakstemaliste) {
        for (int i = 0; i<sakstemaliste.size() - 1; i++) {
            assertThat(sakstemaliste.get(i).compareTo(sakstemaliste.get(i + 1)), lessThan(0));
        }
    }

    public void assertDatoSortert(TemaMedSaker temaMedSaker) {
        for(int i = 0; i < temaMedSaker.saksliste.size() - 1; i++) {
            assertThat(temaMedSaker.saksliste.get(i).opprettetDato.compareTo(temaMedSaker.saksliste.get(i+1).opprettetDato), greaterThan(0));
        }
    }
}