package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class KontorsperreInfoPanel extends Panel {

    public KontorsperreInfoPanel(String id, final InnboksVM innboksVM) {
        super(id);

        setOutputMarkupId(true);
        add(new Label("enhet", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return innboksVM.getValgtTraad().getKontorsperretEnhet().get();
            }
        }));
        add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erKontorsperret();
            }
        }));
    }
}
