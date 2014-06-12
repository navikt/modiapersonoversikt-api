package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.VisningUtils.getStatusKlasse;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.LEST_AV_BRUKER;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        MeldingVM meldingVM = model.getObject();

        WebMarkupContainer statusContainer = new WebMarkupContainer("status-container");

        WebMarkupContainer status = new WebMarkupContainer("statusIndikator");
        status.add(new AttributeModifier("class", getStatusKlasse(meldingVM.melding.status)));

        Label lestDato = new Label("lestDato");
        lestDato.setVisibilityAllowed(meldingVM.melding.status == LEST_AV_BRUKER);

        statusContainer.add(
                status,
                lestDato,
                new Label("melding.status",  new StringResourceModel("widget.${melding.status}", getModel())));

        add(
                new Label("opprettetDato"),
                new Label("avsender", getString(meldingVM.avsender)),
                new Label("melding.tema", new StringResourceModel("${melding.tema}", getModel())),
                statusContainer);
    }
}
