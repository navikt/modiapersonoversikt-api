package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.SakerVM.SAKSTYPE_GENERELL;

public class TestUtils {

    public final static String ID_1 = "id1";
    public final static String ID_2 = "id2";
    public final static String ID_3 = "id3";
    public final static String ID_4 = "id4";

    public final static DateTime DATE_1 = new DateTime().minusDays(1);
    public final static DateTime DATE_2 = new DateTime().minusDays(2);
    public final static DateTime DATE_3 = new DateTime().minusDays(3);
    public final static DateTime DATE_4 = new DateTime().minusDays(4);

    public final static String TEMAGRUPPE_1 = "Arbeidssøker";
    public final static String TEMAGRUPPE_2 = "Barnebidrag";
    public final static String TEMAGRUPPE_3 = "Familie og barn";

    public final static int TRAAD_LENGDE = 3;

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

    public static Melding createMelding(String id, Meldingstype type, DateTime opprettetDato, String tema, String traadId){
        Melding melding = new Melding(id, type, opprettetDato);
        melding.temagruppe = tema;
        melding.traadId = traadId;
        return melding;
    }

    public static Melding opprettMeldingEksempel() {
        return createMelding(ID_1, Meldingstype.SPORSMAL, DateTime.now(), TEMA_1, ID_1);
    }

    public static List<MeldingVM> createMeldingVMer() {
        MeldingVM melding1VM = new MeldingVM(createMelding(ID_1, Meldingstype.SPORSMAL, DATE_3, TEMAGRUPPE_1, ID_1), TRAAD_LENGDE);
        MeldingVM melding2VM = new MeldingVM(createMelding(ID_2, Meldingstype.SAMTALEREFERAT, DATE_2, TEMAGRUPPE_2, ID_1), TRAAD_LENGDE);
        MeldingVM melding3VM = new MeldingVM(createMelding(ID_3, Meldingstype.SAMTALEREFERAT, DATE_1, TEMAGRUPPE_3, ID_1), TRAAD_LENGDE);
        return new ArrayList<>(Arrays.asList(melding1VM, melding2VM, melding3VM));
    }

}
