package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class JournalfortSkiller extends Panel {


    public JournalfortSkiller(String id, final IModel<MeldingVM> model) {
        super(id, new CompoundPropertyModel<>(model));
        setOutputMarkupPlaceholderTag(true);

        add(new Label("journalfortDatoFormatert"));
        add(new Label("melding.journalfortTema"));
        add(new Label("melding.journalfortAvNavIdent"));
        add(new Label("melding.journalfortSaksId"));
        add(visibleIf(new PropertyModel<Boolean>(model, "nyesteMeldingISinJournalfortgruppe")));
    }
}
