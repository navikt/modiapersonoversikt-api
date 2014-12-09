package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel;

public class TestLeggTilbakePanel extends LeggTilbakePanel {

    public TestLeggTilbakePanel(String id, Henvendelse sporsmal) {
        super(id, sporsmal.temagruppe, Optional.<String>none());
    }
}
