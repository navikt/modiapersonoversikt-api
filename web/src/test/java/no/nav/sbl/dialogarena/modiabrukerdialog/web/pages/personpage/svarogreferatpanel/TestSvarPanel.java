package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;

import java.util.ArrayList;

public class TestSvarPanel extends SvarPanel {
    public TestSvarPanel(String id, String fnr, Sporsmal sporsmal) {
        super(id, fnr, sporsmal, new ArrayList<Svar>());
    }
}
