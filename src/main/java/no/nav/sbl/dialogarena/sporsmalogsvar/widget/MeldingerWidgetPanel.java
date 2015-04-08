package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class MeldingerWidgetPanel extends GenericPanel<WidgetMeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<WidgetMeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(
                new StatusIkon("statusIkon", getModelObject()),
                new Label("traadlengde").setVisibilityAllowed(getModelObject().traadlengde > 2),
                new Label("opprettetDato"),
                new Label("meldingstatus", new PropertyModel<String>(getModel(), "melding.statusTekst")),
                new Label("temagruppe", new PropertyModel<String>(getModel(), "melding.temagruppeNavn"))
        );
    }
}
