package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.LeggTilbakePanel;

public class TestLeggTilbakePanel extends LeggTilbakePanel {

    public TestLeggTilbakePanel(String id, Melding sporsmal) {
        super(id, sporsmal.temagruppe, Optional.<String>none());
    }
}
