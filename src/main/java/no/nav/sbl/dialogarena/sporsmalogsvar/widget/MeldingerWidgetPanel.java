package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.components.StatusIkon;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class MeldingerWidgetPanel extends GenericPanel<WidgetMeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<WidgetMeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(
                new Label("traadlengde").setVisibilityAllowed(getModelObject().traadlengde > 2),
                new Label("opprettetDato"),
                new StatusIkon("statusIkon", getModelObject()),
                new Label("meldingstatus", new PropertyModel<String>(getModel(), "melding.statusTekst"))
                        .add(cssClass(getModelObject().melding.statusKlasse)),
                new Label("temagruppe", new PropertyModel<String>(getModel(), "melding.temagruppeNavn"))
        );
    }
}
