package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> meldingVM) {
        super(id, new CompoundPropertyModel<>(meldingVM));
        setOutputMarkupId(true);
        WebMarkupContainer statusContainer = new WebMarkupContainer("status-container");
        WebMarkupContainer status = new WebMarkupContainer("status");
        status.add(new AttributeModifier("class", meldingVM.getObject().getStatusKlasse()));
        statusContainer.add(
                status,
                new Label("lestDato"),
                new Label("statusTekst"));
        add(
                new Label("opprettetDato"),
                new Label("avsender"),
                new Label("tema"),
                statusContainer);
    }
}
