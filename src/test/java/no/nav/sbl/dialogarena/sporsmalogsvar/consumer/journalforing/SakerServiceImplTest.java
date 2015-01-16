package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SakerServiceImpl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakerServiceImplTest {

    private static final String FNR = "fnr";
    private static final String SAKSTYPE_FAG = "Fag";
    private static final String KODEVERK_TEMAKODE = "Tema kode";
    private static final String KODEVERK_TEMANAVN = "Tema navn";
    private static final Map<String, String> KODEVERK_MOCK_MAP = new HashMap<String, String>() {
        {
            put(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "FAGSYSTEMNAVN");
        }
    };
    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put("ARBD", asList("FUL", "AAP"));
            put("FMLI", asList("FOR", "SIK"));
        }
    };
    private static final List<String> EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE = new ArrayList<>(Arrays.asList("FUL", "SER", "SIK", "VEN"));

    @Mock
    private GsakService gsakService;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private LokaltKodeverk lokaltKodeverk;

    private ArrayList<Sak> saksliste;
    private List<String> alleTemaer;
    private List<String> alleTemagrupper;
    private String godkjentTemaSomFinnesIEnTemagruppe;
    private String temagruppeMedEtGodkjentTema;
    private Saker saker;

    @InjectMocks
    private SakerServiceImpl sakerService;

    @Before
    public void setUp() {
        alleTemaer = getAlleEksisterendeTemaer();
        alleTemagrupper = getAlleEksisterendeTemagrupper();
        saksliste = createSakslisteBasertPaTemaMap();
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(saksliste);
        when(gsakKodeverk.hentFagsystemMapping()).thenReturn(KODEVERK_MOCK_MAP);
        when(standardKodeverk.getArkivtemaNavn(KODEVERK_TEMAKODE)).thenReturn(KODEVERK_TEMANAVN);
        when(lokaltKodeverk.hentTemagruppeTemaMapping()).thenReturn(TEMAGRUPPE_TEMA_MAPPING);

        saker = new Saker();
    }

    // Gruppering på tema
    @Test
    public void gittHverSakHarUniktTemaReturnerKorrektSakstemaliste() {
        saker = sakerService.hentSaker(FNR);

        List<SakerForTema> sakerForTemaListe = saker.getSakerListeGenerelle();
        assertThat(sakerForTemaListe.size(), is(4));
        assertThat(sakerForTemaListe.get(0).saksliste.size(), is(1));
        assertThat(sakerForTemaListe.get(1).saksliste.size(), is(1));
        assertThat(sakerForTemaListe.get(2).saksliste.size(), is(1));
        assertThat(sakerForTemaListe.get(3).saksliste.size(), is(1));
    }

    @Test
    public void gittToSakerMedLiktTemaReturnerKorrektSakstemaliste() {
        Sak sak4 = createSak("44444444", alleTemaer.get(0), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(5));
        saksliste.add(sak4);

        saker = sakerService.hentSaker(FNR);

        List<SakerForTema> sakerForTemaListe = saker.getSakerListeGenerelle();
        assertThat(sakerForTemaListe.size(), is(4));
        for (SakerForTema sakerForTema : sakerForTemaListe) {
            if (sakerForTema.temaKode.equals(alleTemaer.get(0))) {
                assertThat(sakerForTema.saksliste.size(), is(2));
            } else {
                assertThat(sakerForTema.saksliste.size(), is(1));
            }
        }
    }

    //Valgt temagruppe øverst
    @Test
    public void gittEnValgtTemagruppeSjekkAtTemaSakerMedSammeTemagruppeSomValgtTraadLiggerForst() {
        saker = sakerService.hentSaker(FNR);
        List<SakerForTema> sakerListeGenerelle = saker.getSakerListeGenerelle().sorter(temagruppeMedEtGodkjentTema);

        assertThat(sakerListeGenerelle.get(0).temagruppe, is(temagruppeMedEtGodkjentTema));
    }

    // Sorterer alfabetisk innen valgt temagruppe
    @Test
    public void gittEnTemagruppeSjekkAtTemaSakerMedSammeTemagruppeSomValgtTraadErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        List<String> traadTemagruppeSineTemaer = TEMAGRUPPE_TEMA_MAPPING.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();
        for (String tema : traadTemagruppeSineTemaer) {
            saksliste.add(createSak("44444444", tema, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(5)));
        }

        saker = sakerService.hentSaker(FNR);
        List<SakerForTema> sakerListeGenerelle = saker.getSakerListeGenerelle().sorter(traadTemagruppe);

        List<SakerForTema> sakerForTemagruppeInnenforValgtTema = sakerListeGenerelle.subList(0, traadTemagruppeLengde);
        assertSortert(sakerForTemagruppeInnenforValgtTema);
    }

    // Sorter resten av listen alfabetisk
    @Test
    public void deSakeneSomIkkeHarTemagruppenTilTraadenErSortertAlfabetisk() {
        String traadTemagruppe = alleTemagrupper.get(0);
        List<String> traadTemagruppeSineTemaer = TEMAGRUPPE_TEMA_MAPPING.get(traadTemagruppe);
        int traadTemagruppeLengde = traadTemagruppeSineTemaer.size();

        saker = sakerService.hentSaker(FNR);
        List<SakerForTema> alleSakerForTemaListe = saker.getSakerListeGenerelle().sorter(traadTemagruppe);

        List<SakerForTema> temaUtenforValgtTemagruppe = alleSakerForTemaListe.subList(traadTemagruppeLengde, alleSakerForTemaListe.size());
        assertSortert(temaUtenforValgtTemagruppe);
    }

    // Sorter etter dato innefor hvert tema
    @Test
    public void sakerInnenforSammeTemaDatoSorteres() {
        ArrayList<Sak> sakslistekloneMedAndreDatoer = new ArrayList<>();
        for (Sak sak : saksliste) {
            sakslistekloneMedAndreDatoer.add(createSak("101010101", sak.temaKode, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(5)));
        }
        saksliste.addAll(sakslistekloneMedAndreDatoer);

        saker = sakerService.hentSaker(FNR);

        List<SakerForTema> sakerForTemaListe = saker.getSakerListeGenerelle();
        for (SakerForTema sakerForTema : sakerForTemaListe) {
            assertDatoSortert(sakerForTema);
        }
    }

    // Sjekk at fagsaker og generelle saker kommer i to ulike lister
    @Test
    public void generelleSakerOgFagsakerErPlassertISinKorresponderendeListe() {
        ArrayList<Sak> sakslistekloneMedAndreSakstyper = new ArrayList<>();
        for (Sak sak : saksliste) {
            sakslistekloneMedAndreSakstyper.add(createSak("101010101", sak.temaKode, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), SAKSTYPE_FAG, sak.opprettetDato));
        }
        saksliste.addAll(sakslistekloneMedAndreSakstyper);

        saker = sakerService.hentSaker(FNR);

        List<SakerForTema> sakerForTemaListeGenerell = saker.getSakerListeGenerelle();
        List<SakerForTema> sakerForTemaListeFag = saker.getSakerListeFagsak();
        assertSakstypeGenerell(sakerForTemaListeGenerell, true);
        assertSakstypeGenerell(sakerForTemaListeFag, false);
    }

    @Test
    public void ingenElementerForsvinnerFraListeMedSakerVedGjentatteKall() {
        saker = sakerService.hentSaker(FNR);

        for (String temagruppe : alleTemagrupper) {
            List<SakerForTema> sakerForTemaListe = saker.getSakerListeGenerelle().sorter(temagruppe);
            assertThat(antallSaker(sakerForTemaListe), is(4));
        }
    }

    @Test
    public void oversetterFagsystemKodeTilFagsystemNavn() {
        Sak sak = createSak("id 1", alleTemaer.get(0), GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "sakstype", DateTime.now().minusDays(1));
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(new ArrayList<>(Arrays.asList(sak)));

        saker = sakerService.hentSaker(FNR);

        Sak sakResultat = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        assertThat(sakResultat.fagsystemNavn, is(KODEVERK_MOCK_MAP.get(sak.fagsystemKode)));
    }

    @Test
    public void setterFagsystemNavnTilFagsystemKodeDersomNavnetIkkeFinnesIMapping() {
        Sak sak = createSak("id 1", alleTemaer.get(0), GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(1), "sakstype", DateTime.now().minusDays(1));
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(new ArrayList<>(Arrays.asList(sak)));

        saker = sakerService.hentSaker(FNR);

        Sak sakResultat = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        assertThat(sakResultat.fagsystemNavn, is(sak.fagsystemKode));
    }

    @Test
    public void oversetterTemaKodeTilTemaNavn() {
        Sak sak = createSak("id 1", KODEVERK_TEMAKODE, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "sakstype", DateTime.now().minusDays(1));
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(new ArrayList<>(Arrays.asList(sak)));

        saker = sakerService.hentSaker(FNR);

        Sak sakResultat = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        assertThat(sakResultat.temaNavn, is(KODEVERK_TEMANAVN));
    }

    @Test
    public void setterTemaNavnTilTemaKodeDersomNavnetIkkeFinnesIKodeverkMapping() {
        String temaKodeSomIkkeFinnesIMapping = "Tema som ikke finnes i mapping";
        Sak sak = createSak("id 1", temaKodeSomIkkeFinnesIMapping, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(1), "sakstype", DateTime.now().minusDays(1));
        when(gsakService.hentSakerForBruker(anyString())).thenReturn(new ArrayList<>(Arrays.asList(sak)));

        saker = sakerService.hentSaker(FNR);

        Sak sakResultat = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        assertThat(sakResultat.temaNavn, is(temaKodeSomIkkeFinnesIMapping));
    }

    private int antallSaker(List<SakerForTema> temasakerListe) {
        int lengde = 0;
        for (SakerForTema sakerForTema : temasakerListe) {
            lengde += sakerForTema.saksliste.size();
        }
        return lengde;
    }

    private void assertSakstypeGenerell(List<SakerForTema> sakerForTemaListe, boolean generell) {
        for (SakerForTema sakerForTema : sakerForTemaListe) {
            for (Sak sak : sakerForTema.saksliste) {
                assertThat(sak.isSakstypeForVisningGenerell(), is(generell));
            }
        }
    }

    private ArrayList<Sak> createSakslisteBasertPaTemaMap() {
        finnTemagruppenTilEtGodkjentTema();
        List<String> unikeTema = new ArrayList<>(EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE);
        unikeTema.remove(godkjentTemaSomFinnesIEnTemagruppe);

        return new ArrayList<>(Arrays.asList(
                createSak("11111111", unikeTema.get(0), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak("22222222", unikeTema.get(1), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak("33333333", godkjentTemaSomFinnesIEnTemagruppe, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak("44444444", unikeTema.get(2), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(2))
        ));
    }

    private void finnTemagruppenTilEtGodkjentTema() {
        for (String godkjentTema : EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE) {
            for (String temagruppe : TEMAGRUPPE_TEMA_MAPPING.keySet()) {
                if (TEMAGRUPPE_TEMA_MAPPING.get(temagruppe).contains(godkjentTema)) {
                    temagruppeMedEtGodkjentTema = temagruppe;
                    godkjentTemaSomFinnesIEnTemagruppe = godkjentTema;
                    return;
                }
            }
        }
    }

    private ArrayList<String> getAlleEksisterendeTemaer() {
        Collection<List<String>> values = TEMAGRUPPE_TEMA_MAPPING.values();
        ArrayList<String> strings = new ArrayList<>();
        for (List<String> value : values) {
            strings.addAll(value);
        }
        return strings;
    }

    private ArrayList<String> getAlleEksisterendeTemagrupper() {
        return new ArrayList<>(TEMAGRUPPE_TEMA_MAPPING.keySet());
    }

    private void assertSortert(List<SakerForTema> sakerForTemaListe) {
        for (int i = 0; i < sakerForTemaListe.size() - 1; i++) {
            assertThat(sakerForTemaListe.get(i).compareTo(sakerForTemaListe.get(i + 1)), lessThan(0));
        }
    }

    public void assertDatoSortert(SakerForTema sakerForTema) {
        for (int i = 0; i < sakerForTema.saksliste.size() - 1; i++) {
            assertThat(sakerForTema.saksliste.get(i).opprettetDato.compareTo(sakerForTema.saksliste.get(i + 1).opprettetDato), greaterThanOrEqualTo(0));
        }
    }
}