package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class BehandlingsPanel extends Panel {

    public BehandlingsPanel(String id, Model<GenerellBehandling> behandlingModel) {
        super(id, behandlingModel);
    }

}
