package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;

public class SaksbehandlerInnstillingerTestPage extends BasePage {

    public SaksbehandlerInnstillingerTestPage() {
        SaksbehandlerInnstillingerPanel saksbehandlerinnstillinger = new SaksbehandlerInnstillingerPanel("saksbehandlerinnstillinger");
        add(saksbehandlerinnstillinger);
        add(new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerinnstillingertoggler", saksbehandlerinnstillinger.getMarkupId()));
    }
}
