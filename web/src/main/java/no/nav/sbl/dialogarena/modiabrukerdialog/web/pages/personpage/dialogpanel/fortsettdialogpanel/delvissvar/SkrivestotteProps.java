package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;

import java.io.Serializable;
import java.util.HashMap;

import static java.util.Arrays.asList;

public class SkrivestotteProps extends HashMap<String, Object> implements Serializable {

    private static final String KONTAKTSENTER_SKRIVESTOT_TETAG = "ks";

    public SkrivestotteProps(GrunnInfo grunnInfo, String saksbehandlerValgtEnhet) {
        put("autofullfor", grunnInfo);

        if (isKontaktsenter(saksbehandlerValgtEnhet)) {
            put("knagger", asList(KONTAKTSENTER_SKRIVESTOT_TETAG));
        }
    }

    private boolean isKontaktsenter(String saksbehandlerValgtEnhet) {
        return saksbehandlerValgtEnhet.startsWith("41");
    }
}
