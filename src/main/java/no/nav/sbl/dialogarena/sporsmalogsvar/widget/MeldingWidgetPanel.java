package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.getStatusKlasse;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(
                new Label("opprettetDato"),
                new Label("avsender", new StringResourceModel("${avsender}", getModel())),
                new Label("temagruppe", new StringResourceModel("${temagruppeKey}", getModel())),
                new WebMarkupContainer("statusIndikator")
                        .add(cssClass(getStatusKlasse(getModelObject().melding.status))),
                new Label("melding.status", new StringResourceModel("widget.${melding.status}", getModel()))
        );
    }
}
