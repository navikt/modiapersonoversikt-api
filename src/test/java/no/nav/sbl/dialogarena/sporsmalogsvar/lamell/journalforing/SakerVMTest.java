package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.SakerVM.TEMA_MAPPING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.TestUtils.createSak;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SakerVMTest {

    public final static String SAKSTYPE_FAG = "Fag";
    private final static String TEMA_OPPFOLGING = "Oppfølging";

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
        sakerVM.oppdater();

        List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        assertThat(temaSakerListe.size(), is(4));
        assertThat(temaSakerListe.get(0).saksliste.size(), is(1));
        assertThat(temaSakerListe.get(1).saksliste.size(), is(1));
        assertThat(temaSakerListe.get(2).saksliste.size(), is(1));
        assertThat(temaSakerListe.get(3).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", alleTemaer.get(0), "Fagsak 4", SAKSTYPE_GENERELL, DateTime.now().minusDays(5));
        saksliste.add(sak4);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        assertThat(temaSakerListe.size(), is(4));
        for (TemaSaker temaSaker : temaSakerListe) {
            if (temaSaker.tema.equals(alleTemaer.get(0))) {
                assertThat(temaSaker.saksliste.size(), is(2));
            } else {
                assertThat(temaSaker.saksliste.size(), is(1));
            }
        }
    }

    //Valgt temgruppe øverst
    @Test
    public void gittValgtTemagruppe0sjekkAtTemaSakerMedSammeTemagruppeSomValgtTraadLiggerForst() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;

        List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        assertThat(temaSakerListe.get(0).temagruppe, is(traadTemagruppe));
        assertThat(temaSakerListe.get(0).saksliste.size(), is(1));
    }

    @Test
    public void gittValgtTemagruppe2sjekkAtTemaSakerMedSammeTemagruppeSomValgtTraadLiggerForst() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();
        String traadTemagruppe = alleTemagrupper.get(2);
        meldingVM.melding.temagruppe = traadTemagruppe;

        List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        assertThat(temaSakerListe.get(0).temagruppe, is(traadTemagruppe));
        assertThat(temaSakerListe.get(0).saksliste.size(), is(1));
    }

    // Sorterer alfabetisk innen valgt temagruppe
    @Test
    public void gittEnTemagruppeSjekkAtTemaSakerMedSammeTemagruppeSomValgtTraadErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;
        List<String> traadTemagruppeSineTemaer = TEMA_MAPPING.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();
        for (String tema : traadTemagruppeSineTemaer) {
            saksliste.add(createSak("44444444", tema, "Fagsystem 4", SAKSTYPE_GENERELL, DateTime.now().minusDays(5)));
        }
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> temaSakerInnenforValgtTemagruppe = sakerVM.getGenerelleSakerGruppertPaaTema().subList(0, traadTemagruppeLengde);

        assertSortert(temaSakerInnenforValgtTemagruppe);
    }

    // Sorter resten av listen alfabetisk
    @Test
    public void sjekkAtDeSakeneSomIkkeHarTemagruppenTilTraadenErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        meldingVM.melding.temagruppe = traadTemagruppe;
        List<String> traadTemagruppeSineTemaer = TEMA_MAPPING.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> alleTemaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        List<TemaSaker> temaUtenforValgtTemagruppe = alleTemaSakerListe.subList(traadTemagruppeLengde, alleTemaSakerListe.size());
        assertSortert(temaUtenforValgtTemagruppe);
    }

    // Sorter etter dato innefor hvert tema
    @Test
    public void sjekkDatoSorteringInnenforSammeTema() {
        ArrayList<Sak> sakslistekloneMedAndreDatoer = new ArrayList<>();
        for (Sak sak : saksliste) {
            sakslistekloneMedAndreDatoer.add(createSak("101010101", sak.tema, "Fagsystem", SAKSTYPE_GENERELL, DateTime.now().minusDays(5)));
        }
        saksliste.addAll(sakslistekloneMedAndreDatoer);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

        for (TemaSaker temaSaker : temaSakerListe) {
            assertDatoSortert(temaSaker);
        }
    }

    // Sjekk at fagsaker og generelle saker kommer i to ulike lister
    @Test
    public void sjekkAtGenerelleSakerOgFagsakerErPlassertISinKorresponderendeListe() {
        ArrayList<Sak> sakslistekloneMedAndreSakstyper = new ArrayList<>();
        for (Sak sak : saksliste) {
            sakslistekloneMedAndreSakstyper.add(createSak("101010101", sak.tema, "Fagsystem", SAKSTYPE_FAG, sak.opprettetDato));
        }
        saksliste.addAll(sakslistekloneMedAndreSakstyper);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> temaSakerListeGenerell = sakerVM.getGenerelleSakerGruppertPaaTema();
        List<TemaSaker> temaSakerListeFag = sakerVM.getFagsakerGruppertPaaTema();

        assertSakstypeGenerell(temaSakerListeGenerell, true);
        assertSakstypeGenerell(temaSakerListeFag, false);
    }

    @Test
    public void sjekkAtGenrellSakMedTemaOppfolgingErPlassertIFagsakerListen() {
        Sak sakMedTemaOppfolging = createSak("15472473245", TEMA_OPPFOLGING, "Fagsystem 3", SAKSTYPE_GENERELL, DateTime.now().minusDays(4));
        Sak sakMedSakstypeFagsak = createSak("15472473245", alleTemaer.get(3), "Fagsystem 3", SAKSTYPE_FAG, DateTime.now().minusDays(4));
        saksliste.add(sakMedSakstypeFagsak);
        saksliste.add(sakMedTemaOppfolging);
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        List<TemaSaker> temaSakerListeFag = sakerVM.getFagsakerGruppertPaaTema();

        boolean containsSakMedTemaOppfolging = false;
        for (TemaSaker temaSaker : temaSakerListeFag) {
            containsSakMedTemaOppfolging = temaSaker.saksliste.contains(sakMedTemaOppfolging);
        }
        assertTrue(containsSakMedTemaOppfolging);
    }

    @Test
    public void sjekkAtIngenElementerForsvinnerFraListeMedSakerVedGjentatteKall() {
        SakerVM sakerVM = new SakerVM(innboksVM, meldingService);
        sakerVM.oppdater();

        for (int i = 0; i < 4; i++) {
            meldingVM.melding.temagruppe = alleTemagrupper.get(i);

            List<TemaSaker> temaSakerListe = sakerVM.getGenerelleSakerGruppertPaaTema();

            assertThat(antallSaker(temaSakerListe), is(4));
        }
    }

    private int antallSaker(List<TemaSaker> temasakerListe) {
        int lengde = 0;
        for (TemaSaker temaSaker : temasakerListe) {
            lengde += temaSaker.saksliste.size();
        }
        return lengde;
    }

    private void assertSakstypeGenerell(List<TemaSaker> temaSakerListe, boolean generell) {
        for (TemaSaker temaSaker : temaSakerListe) {
            for (Sak sak : temaSaker.saksliste) {
                assertThat(sak.isSakstypeForVisingGenerell(), is(generell));
            }
        }
    }

    private ArrayList<Sak> createSakslisteBasertPaTemaMap() {
        return new ArrayList<>(Arrays.asList(
                createSak("111111111", alleTemaer.get(0), "Fagsystem1", SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak("22222222", alleTemaer.get(2), "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak("33333333", alleTemaer.get(4), "Fagsystem3", SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak("44444444", alleTemaer.get(6), "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(2))
        ));
    }

    private ArrayList<String> getAlleEksisterendeTemaer() {
        Collection<List<String>> values = TEMA_MAPPING.values();
        ArrayList<String> strings = new ArrayList<>();
        for (List<String> value : values) {
            strings.addAll(value);
        }
        return strings;
    }

    private ArrayList<String> getAlleEksisterendeTemagrupper() {
        return new ArrayList<>(TEMA_MAPPING.keySet());
    }

    private void assertSortert(List<TemaSaker> temaSakerListe) {
        for (int i = 0; i < temaSakerListe.size() - 1; i++) {
            assertThat(temaSakerListe.get(i).compareTo(temaSakerListe.get(i + 1)), lessThan(0));
        }
    }

    public void assertDatoSortert(TemaSaker temaSaker) {
        for (int i = 0; i < temaSaker.saksliste.size() - 1; i++) {
            assertThat(temaSaker.saksliste.get(i).opprettetDato.compareTo(temaSaker.saksliste.get(i + 1).opprettetDato), greaterThanOrEqualTo(0));
        }
    }

}