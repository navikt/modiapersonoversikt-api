package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FeilsendtInfoPanel extends Panel {

    public FeilsendtInfoPanel(String id, InnboksVM innboksVM) {
        super(id, new PropertyModel<>(innboksVM, "valgtTraad"));
        setOutputMarkupId(true);

        add(new Label("markertAv", new PropertyModel<>(getDefaultModel(), "markertSomFeilsendtAv.get()")));
        add(visibleIf(new PropertyModel<Boolean>(getDefaultModel(), "erFeilsendt")));
    }
}
