package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class JournalfortSkiller extends Panel {


    public JournalfortSkiller(String id, final IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<MeldingVM>(model));

        setOutputMarkupPlaceholderTag(true);

        add(new Label("journalfortDatoFormatert", new Model<String>(){
            @Override
            public String getObject() {
                return model.getObject().getJournalfortDatoFormatert();
            }
        }));
        add(new Label("melding.journalfortTema"));
        add(visibleIf(
            new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return model.getObject().nyesteMeldingISinJournalfortgruppe;
            }
        }));
    }
}
