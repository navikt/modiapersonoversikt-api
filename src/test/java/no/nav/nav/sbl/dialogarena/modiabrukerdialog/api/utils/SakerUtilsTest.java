package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakerUtilsTest {
    private static final String SAKSTYPE_FAG = "Fag";
    private static final String KODEVERK_TEMAKODE = "Tema kode";
    private static final String KODEVERK_TEMANAVN = "Tema navn";
    public static final Map<String, List<String>> TEMAGRUPPE_TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put("ARBD", asList("FUL", "AAP"));
            put("FMLI", asList("FOR", "BID"));
        }
    };
    private static final Map<String, String> KODEVERK_MOCK_MAP = new HashMap<String, String>() {
        {
            put(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "FAGSYSTEMNAVN");
        }
    };
    private static final List<String> EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE = new ArrayList<>(asList("FUL", "SER", "SYM", "VEN"));

    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;

    private ArrayList<Sak> saksliste;
    private List<String> alleTemaer;
    private List<String> alleTemagrupper;
    private String godkjentTemaSomFinnesIEnTemagruppe;

    @Before
    public void setup() {
        alleTemaer = getAlleEksisterendeTemaer();
        saksliste = createSakslisteBasertPaTemaMap();
        alleTemagrupper = getAlleEksisterendeTemagrupper();

        when(gsakKodeverk.hentFagsystemMapping()).thenReturn(KODEVERK_MOCK_MAP);
        when(standardKodeverk.getArkivtemaNavn(KODEVERK_TEMAKODE)).thenReturn(KODEVERK_TEMANAVN);
    }


    @Test
    public void oversetterFagsystemKodeTilFagsystemNavn() {
        Sak sak = createSak("id 1", alleTemaer.get(0), GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "sakstype", DateTime.now().minusDays(1));
        List<Sak> sakerForBruker = asList(sak);

        SakerUtils.leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);

        assertThat(sak.fagsystemNavn, is(KODEVERK_MOCK_MAP.get(sak.fagsystemKode)));
    }

    @Test
    public void setterFagsystemNavnTilFagsystemKodeDersomNavnetIkkeFinnesIMapping() {
        Sak sak = createSak("id 1", alleTemaer.get(0), GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(1), "sakstype", DateTime.now().minusDays(1));
        List<Sak> sakerForBruker = asList(sak);

        SakerUtils.leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);

        assertThat(sak.fagsystemNavn, is(sak.fagsystemKode));
    }

    @Test
    public void oversetterTemaKodeTilTemaNavn() {
        Sak sak = createSak("id 1", KODEVERK_TEMAKODE, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0), "sakstype", DateTime.now().minusDays(1));
        List<Sak> sakerForBruker = asList(sak);

        SakerUtils.leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);

        assertThat(sak.temaNavn, is(KODEVERK_TEMANAVN));
    }

    @Test
    public void setterTemaNavnTilTemaKodeDersomNavnetIkkeFinnesIKodeverkMapping() {
        String temaKodeSomIkkeFinnesIMapping = "Tema som ikke finnes i mapping";
        Sak sak = createSak("id 1", temaKodeSomIkkeFinnesIMapping, GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(1), "sakstype", DateTime.now().minusDays(1));
        List<Sak> sakerForBruker = asList(sak);

        SakerUtils.leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);

        assertThat(sak.temaNavn, is(temaKodeSomIkkeFinnesIMapping));
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

    private ArrayList<Sak> createSakslisteBasertPaTemaMap() {
        finnTemagruppenTilEtGodkjentTema();
        List<String> unikeTema = new ArrayList<>(EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE);
        unikeTema.remove(godkjentTemaSomFinnesIEnTemagruppe);

        ArrayList<Sak> liste = new ArrayList<>(asList(
                createSak("11111111", unikeTema.get(0), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak("22222222", unikeTema.get(1), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak("33333333", godkjentTemaSomFinnesIEnTemagruppe, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak("44444444", unikeTema.get(2), GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(2)),
                createSak("55555555", "AAP", FAGSYSTEMKODE_ARENA, SAKSTYPE_FAG, DateTime.now().minusDays(5))
        ));

        SakerUtils.leggTilFagsystemnavnOgTemanavn(liste, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);

        return liste;
    }

    private void finnTemagruppenTilEtGodkjentTema() {
        for (String godkjentTema : EKSEMPLER_PAA_GODKJENTE_TEMAER_FOR_GENERELLE) {
            for (String temagruppe : TEMAGRUPPE_TEMA_MAPPING.keySet()) {
                if (TEMAGRUPPE_TEMA_MAPPING.get(temagruppe).contains(godkjentTema)) {
                    godkjentTemaSomFinnesIEnTemagruppe = godkjentTema;
                    return;
                }
            }
        }
    }

    private static Sak createSak(String saksId, String temaKode, String fagsystemKode, String sakstype, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = optional(saksId);
        sak.temaKode = temaKode;
        sak.fagsystemKode = fagsystemKode;
        if (sakstype.equals(SAKSTYPE_GENERELL)) {
            sak.sakstype = sakstype;
        } else {
            sak.sakstype = temaKode;
        }

        sak.opprettetDato = opprettet;
        return sak;
    }

    private void assertSakstypeGenerell(List<SakerForTema> sakerForTemaListe, boolean generell) {
        for (SakerForTema sakerForTema : sakerForTemaListe) {
            for (Sak sak : sakerForTema.saksliste) {
                assertThat(sak.isSakstypeForVisningGenerell(), is(generell));
            }
        }
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

    private int antallSaker(List<SakerForTema> temasakerListe) {
        int lengde = 0;
        for (SakerForTema sakerForTema : temasakerListe) {
            lengde += sakerForTema.saksliste.size();
        }
        return lengde;
    }
}