package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.domain.TemaVM;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class SaksWidgetPanel extends GenericPanel<TemaVM> {

    public SaksWidgetPanel(String id, IModel<TemaVM> model) {
        super(id, model);
    }
}
