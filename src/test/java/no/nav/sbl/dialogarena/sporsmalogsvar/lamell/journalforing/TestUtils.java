package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.SakerVM.SAKSTYPE_GENERELL;

public class TestUtils {

    public final static String TEMA_1 = "Pensjon";
    public final static String TEMA_2 = "Dagpenger";
    public final static String TEMA_3 = "Barnebidrag";

    public static ArrayList<Sak> createMockSaksliste(){
        return new ArrayList<>(Arrays.asList(
                createSak("111111111", TEMA_1, "Fagsystem1", SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak("22222222", TEMA_2, "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(3)),
                createSak("33333333", TEMA_3, "Fagsystem3", SAKSTYPE_GENERELL, DateTime.now().minusDays(9)),
                createSak("44444444", TEMA_1, "Fagsystem2", SAKSTYPE_GENERELL, DateTime.now().minusDays(2))));

    }

    public static Sak createSak(String saksId, String tema, String fagsak, String sakstype, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
        sak.tema = tema;
        sak.fagsystem = fagsak;
        if (sakstype.equals(SAKSTYPE_GENERELL)) {
            sak.sakstype = sakstype;
        } else {
            sak.sakstype = tema;
        }

        sak.opprettetDato = opprettet;
        return sak;
    }

    public static Melding opprettMockMelding() {
        Melding melding = new Melding("", Meldingstype.SPORSMAL, DateTime.now());
        melding.temagruppe = "temagruppe";
        return melding;
    }

}
