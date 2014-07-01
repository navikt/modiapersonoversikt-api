package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class SaksWidgetPanel extends GenericPanel<TemaVM> {

    public SaksWidgetPanel(String id, IModel<TemaVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        TemaVM temaVM = model.getObject();

        add(
                new Label("temaTittel", temaVM.getType()),
                new Label("temaDato", "Siste oppdaterte dato: " + temaVM.sistoppdaterteBehandling.behandlingDato)

        );
    }
}
