package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class MeldingerWidgetPanel extends GenericPanel<WidgetMeldingVM> {

    public MeldingerWidgetPanel(String id, IModel<WidgetMeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupId(true);

        add(new WidgetMeldingDetaljer("widgetMeldingDetaljer", getModelObject()));
    }
}