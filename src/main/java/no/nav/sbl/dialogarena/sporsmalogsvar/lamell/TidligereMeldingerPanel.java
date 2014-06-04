package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;


import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

public class TidligereMeldingerPanel extends Panel {
    public TidligereMeldingerPanel(String id) {
        super(id);
        add(new PropertyListView<MeldingVM>("tidligereMeldinger") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                item.add(new AvsenderBilde("avsenderbilde", item.getModelObject()));
                item.add(new Label("opprettetDato"));
                item.add(new Label("tema", new StringResourceModel("${melding.tema}", item.getModel())));
                item.add(new URLParsingMultiLineLabel("melding.fritekst"));
            }
        });
    }
}
