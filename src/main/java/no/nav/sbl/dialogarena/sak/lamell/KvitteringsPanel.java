package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class KvitteringsPanel extends Panel {

    public KvitteringsPanel(String id, Model<GenerellBehandling> kvitteringsModel) {
        super(id, kvitteringsModel);
    }

}
