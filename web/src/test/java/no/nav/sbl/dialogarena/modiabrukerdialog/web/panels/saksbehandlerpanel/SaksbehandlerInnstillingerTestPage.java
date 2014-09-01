package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;

public class SaksbehandlerInnstillingerTestPage extends BasePage {

    public SaksbehandlerInnstillingerTestPage() {
        add(new SaksbehandlerInnstillingerPanel("saksbehandlerinnstillinger"));
        add(new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerinnstillingertoggler"));
    }
}
