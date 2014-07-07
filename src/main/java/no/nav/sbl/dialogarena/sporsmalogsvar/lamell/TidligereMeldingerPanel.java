package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;


import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;

public class TidligereMeldingerPanel extends Panel {
    public TidligereMeldingerPanel(String id) {
        super(id);
        add(new PropertyListView<MeldingVM>("valgtTraad.tidligereMeldinger") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                final MeldingVM meldingVM = item.getModelObject();
                WebMarkupContainer journalfortSkiller = new WebMarkupContainer("journalfortSkiller");
                journalfortSkiller.add(new Label("melding.journalfortDato", Datoformat.kortMedTid(meldingVM.melding.journalfortDato)));
                if (!meldingVM.nyesteMeldingISinJournalfortgruppe) {
                    journalfortSkiller.setVisible(false);
                }
                item.add(journalfortSkiller);
                item.add(new AvsenderBilde("avsenderbilde", meldingVM));
                item.add(new Label("opprettetDato"));
                item.add(new Label("temagruppe", new StringResourceModel("${melding.temagruppe}", item.getModel())));
                item.add(new Label("melding.navIdent").add(enabledIf(new AbstractReadOnlyModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return meldingVM.melding.navIdent!= null && !meldingVM.melding.navIdent.isEmpty();
                    }
                })));
                item.add(new URLParsingMultiLineLabel("melding.fritekst"));
            }
        });
    }
}
