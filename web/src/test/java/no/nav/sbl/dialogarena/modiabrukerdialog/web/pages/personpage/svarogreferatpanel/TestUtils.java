package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import org.joda.time.DateTime;

import java.util.*;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.GODKJENTE_TEMA_FOR_GENERELLE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.SAKSTYPE_GENERELL;

public class TestUtils {

    public final static String SAKS_ID_1 = "11111111";
    public final static String SAKS_ID_2 = "22222222";
    public final static String SAKS_ID_3 = "33333333";

    private static final String SAKSTYPE_FAG = "Fag";

    public final static String TEMA_1 = GODKJENTE_TEMA_FOR_GENERELLE.get(0);
    public final static String TEMA_2 = GODKJENTE_TEMA_FOR_GENERELLE.get(1);

    public static Saker createMockSaker() {
        List<Sak> generelleSakerTema1 = new ArrayList<>(Arrays.asList(
                createSak(SAKS_ID_1, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak(SAKS_ID_2, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4))));
        SakerForTema sakerforTemaGenerelle = new SakerForTema(TEMA_1, "navn", "temagruppe", generelleSakerTema1);
        SakerListe sakerListeGenerelle = new SakerListe(Arrays.asList(sakerforTemaGenerelle));

        List<Sak> fagSakerTema2 = new ArrayList<>(Arrays.asList(
                createSak(SAKS_ID_3, TEMA_2, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_FAG, DateTime.now().minusDays(4))));
        SakerForTema sakerforTemaFagsaker = new SakerForTema(TEMA_2, "navn", "temagruppe", fagSakerTema2);
        SakerListe sakerListeFagsaker = new SakerListe(Arrays.asList(sakerforTemaFagsaker));

        return new Saker(sakerListeFagsaker, sakerListeGenerelle);
    }

    public static Sak createSak(String saksId, String temaKode, String fagsystemKode, String sakstype, DateTime opprettet) {
        Sak sak = new Sak();
        sak.saksId = saksId;
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
}
