package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class MeldingWidgetPanel extends GenericPanel<MeldingVM> {

    public MeldingWidgetPanel(String id, IModel<MeldingVM> meldingVM) {
        super(id, new CompoundPropertyModel<>(meldingVM));
        setOutputMarkupId(true);
        add(
                new Label("dato"),
                new Label("avsender"),
                new Label("tema"));
    }
}
