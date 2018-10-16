package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.Component;

public class NyUtbetalingLerret extends Lerret {

    public NyUtbetalingLerret(String id, Component comp) {
        super(id);
        add(comp);
    }
}
