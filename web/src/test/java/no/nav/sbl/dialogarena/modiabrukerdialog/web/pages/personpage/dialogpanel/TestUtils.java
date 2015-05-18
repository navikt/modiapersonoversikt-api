package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerListe;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;

public class TestUtils {

    public final static String SAKS_ID_1 = "11111111";
    public final static String SAKS_ID_2 = "22222222";
    public final static String SAKS_ID_3 = "33333333";

    private static final String SAKSTYPE_FAG = "Fag";

    public final static String TEMA_1 = GODKJENTE_TEMA_FOR_GENERELLE.get(0);
    public final static String TEMA_2 = GODKJENTE_TEMA_FOR_GENERELLE.get(1);

    public static Saker createMockSaker() {
        List<Sak> generelleSakerTema1 = new ArrayList<>(asList(
                createSak(SAKS_ID_1, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4)),
                createSak(SAKS_ID_2, TEMA_1, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4))));
        SakerForTema sakerforTemaGenerelle = new SakerForTema(TEMA_1, "navn", generelleSakerTema1);
        SakerListe sakerListeGenerelle = new SakerListe(asList(sakerforTemaGenerelle));

        List<Sak> fagSakerTema2 = new ArrayList<>(asList(
                createSak(SAKS_ID_3, TEMA_2, GODKJENT_FAGSYSTEM_FOR_GENERELLE, SAKSTYPE_FAG, DateTime.now().minusDays(4))));
        SakerForTema sakerforTemaFagsaker = new SakerForTema(TEMA_2, "navn", fagSakerTema2);
        SakerListe sakerListeFagsaker = new SakerListe(asList(sakerforTemaFagsaker));

        return new Saker(sakerListeFagsaker, sakerListeGenerelle);
    }

    public static Sak createSak(String saksId, String temaKode, String fagsystemKode, String sakstype, DateTime opprettet) {
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
}
