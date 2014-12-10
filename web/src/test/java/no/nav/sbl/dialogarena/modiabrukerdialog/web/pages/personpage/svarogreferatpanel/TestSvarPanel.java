package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;

import java.util.ArrayList;
import java.util.List;

public class TestSvarPanel extends SvarPanel {
    public TestSvarPanel(String id, String fnr, List<Henvendelse> traad) {
        super(id, fnr, traad, Optional.<String>none());
    }
}
