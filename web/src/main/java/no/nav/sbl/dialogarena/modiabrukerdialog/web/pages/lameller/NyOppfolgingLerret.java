package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.Component;

public class NyOppfolgingLerret extends Lerret {

    public NyOppfolgingLerret(String id, Component comp) {
        super(id);
        add(comp);
    }
}
