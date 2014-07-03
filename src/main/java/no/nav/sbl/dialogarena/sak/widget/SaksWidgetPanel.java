package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.basic.Label;
import org.joda.time.DateTime;

public class SaksWidgetPanel extends GenericPanel<TemaVM> {

    public SaksWidgetPanel(String id, IModel<TemaVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        TemaVM temaVM = model.getObject();

        DateTime sistOppda = temaVM.sistoppdaterteBehandling.behandlingDato;
        add(
                new Label("temaTittel", temaVM.temakode),
                new Label("temaDato", "Siste oppdaterte dato: " + sistOppda.getDayOfMonth() + "." + sistOppda.getMonthOfYear() + "." + sistOppda.getYear())

        );
    }
}
