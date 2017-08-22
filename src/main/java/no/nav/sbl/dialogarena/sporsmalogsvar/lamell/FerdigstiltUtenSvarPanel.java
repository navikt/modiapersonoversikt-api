package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class FerdigstiltUtenSvarPanel extends Panel {

    public FerdigstiltUtenSvarPanel(String id, IModel<TraadVM> traadVM) {
        super(id, traadVM);
        setOutputMarkupId(true);

        add(visibleIf(Model.of(traadVM.getObject().erFerdigstiltUtenSvar())));

    }
}
