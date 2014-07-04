package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.basic.Label;
import org.joda.time.DateTime;

public class SaksoversiktWidgetPanel extends GenericPanel<TemaVM> {

    public SaksoversiktWidgetPanel(String id, IModel<TemaVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        TemaVM temaVM = model.getObject();

        DateTime sistOppdatert = temaVM.sistoppdaterteBehandling.behandlingDato;
        add(
                new Label("temaTittel", temaVM.temakode),
                new Label("temaDato", "Siste oppdaterte dato: " + sistOppdatert.getDayOfMonth() + "." + sistOppdatert.getMonthOfYear() + "." + sistOppdatert.getYear())

        );
    }
}
