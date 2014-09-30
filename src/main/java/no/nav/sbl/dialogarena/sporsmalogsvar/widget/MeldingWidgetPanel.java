package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(
                new Label("meldingstatus", new StringResourceModel("${meldingStatusTekstKey}", getModel()))
                        .add(cssClass(getModelObject().getStatusIkonKlasse())),
                new Label("opprettetDato"),
                new Label("temagruppe", new StringResourceModel("${temagruppeKey}", getModel()))
        );
    }
}
