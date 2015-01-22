package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.SvarPanel;

import java.util.List;

public class TestSvarPanel extends SvarPanel {
    public TestSvarPanel(String id, String fnr, List<Melding> traad) {
        super(id, fnr, traad, Optional.<String>none());
    }
}
