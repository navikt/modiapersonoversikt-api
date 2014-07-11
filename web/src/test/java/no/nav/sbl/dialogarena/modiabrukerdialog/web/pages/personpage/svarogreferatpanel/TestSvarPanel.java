package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;

import java.util.ArrayList;
import java.util.List;

public class TestSvarPanel extends SvarPanel {
    public TestSvarPanel(String id, String fnr, Sporsmal sporsmal) {
        this(id, fnr, sporsmal, new ArrayList<Svar>());
    }

    public TestSvarPanel(String id, String fnr, Sporsmal sporsmal, List<Svar> svar) {
        super(id, fnr, sporsmal, svar, Optional.<String>none());
    }
}
