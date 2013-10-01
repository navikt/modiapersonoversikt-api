package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Status.LEST_AV_BRUKER;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> meldingVM) {
        super(id, new CompoundPropertyModel<>(meldingVM));
        setOutputMarkupId(true);
        WebMarkupContainer statusContainer = new WebMarkupContainer("status-container");
        WebMarkupContainer status = new WebMarkupContainer("status");
        status.add(new AttributeModifier("class", meldingVM.getObject().getStatusKlasse()));
        Label lestDato = new Label("lestDato");
        lestDato.add(visibleIf(meldingVM.getObject().harStatus(LEST_AV_BRUKER)));
        statusContainer.add(
                status,
                lestDato,
                new Label("statusTekst"));
        add(
                new Label("opprettetDato"),
                new Label("avsender"),
                new Label("tema", new StringResourceModel("${tema}", getModel())),
                statusContainer);
    }
}
