package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class MeldingerWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(
                new Label("meldingstatus", new PropertyModel<String>(getModel(), "melding.statusTekst"))
                        .add(cssClass(getModelObject().melding.statusKlasse)),
                new Label("opprettetDato"),
                new Label("temagruppe", new PropertyModel<String>(getModel(), "melding.temagruppeNavn"))
        );
    }
}
