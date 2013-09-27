package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.InnboksVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final CompoundPropertyModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);
        add(new Label("tema", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return new StringResourceModel(model.getObject().getNyesteHenvendelse().henvendelse.tema, TraaddetaljerPanel.this, null).getString();
            }
        }));
        add(new NyesteHenvendelsePanel("nyeste-henvendelse"));
        add(new TidligereHenvendelserPanel("tidligere-henvendelser"));
    }
}
