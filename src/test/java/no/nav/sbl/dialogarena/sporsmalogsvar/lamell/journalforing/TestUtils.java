package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;

public class TestUtils {

    public final static String TEMA_1 = "Pensjon";
    public final static String TEMA_2 = "Dagpenger";
    public final static String TEMA_3 = "Barnebidrag";

    public static ArrayList<Sak> createMockSaksliste(){
        return new ArrayList<>(Arrays.asList(
                createSak("111111111", TEMA_1, "Fagsak 1", DateTime.now().minusDays(1)),
                createSak("222222222", TEMA_2, "Fagsak 2", DateTime.now().minusDays(4)),
                createSak("333333333", TEMA_3, "Fagsak 1", DateTime.now().minusDays(4))));
    }

    public static Sak createSak(String saksId, String tema, String fagsak, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.fagsystem = fagsak;
        sak.opprettetDato = opprettet;
        sak.tema = tema;
        return sak;
    }

    public static Melding opprettMockMelding() {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.temagruppe = "temagruppe";
        return melding;
    }
}
