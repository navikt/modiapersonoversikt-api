package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;


import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyString;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class TidligereMeldingerPanel extends Panel {
    public TidligereMeldingerPanel(String id, InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(innboksVM));
        add(new PropertyListView<MeldingVM>("valgtTraad.tidligereMeldinger") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();
                item.add(new JournalfortSkiller("journalfortSkiller", item.getModel()));
                item.add(new AvsenderBilde("avsenderbilde", meldingVM));
                item.add(new Label("opprettetDato"));
                item.add(new Label("temagruppe", new ResourceModel(meldingVM.melding.temagruppe)));
                item.add(new Label("melding.navIdent").add(visibleIf(not(isEmptyString(new PropertyModel<String>(meldingVM, "melding.navIdent"))))));
                item.add(new URLParsingMultiLineLabel("melding.fritekst"));
            }
        });
    }
}
