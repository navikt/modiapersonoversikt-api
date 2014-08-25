package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;

public class SaksbehandlerInstillingerTestPage extends BasePage {

    public SaksbehandlerInstillingerTestPage() {
        add(new SaksbehandlerInnstillingerPanel("saksbehandlerinstillinger"));
        add(new SaksbehandlerInstillingerTogglerPanel("saksbehandlerinstillingertoggler"));
    }
}
