package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.SAKSTYPE_GENERELL;

public class TestUtils {

    public final static String SAKS_ID_1 = "11111111";
    public final static String SAKS_ID_2 = "22222222";
    public final static String SAKS_ID_3 = "33333333";

    private static final String SAKSTYPE_FAG = "Fag";

    public final static String TEMA_1 = GODKJENTE_TEMA_FOR_GENERELL_SAK.get(0);
    public final static String TEMA_2 = GODKJENTE_TEMA_FOR_GENERELL_SAK.get(1);


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
