package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<MeldingVM>("tidligereMeldinger") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                item.add(new Label("opprettetDato"));
                item.add(new Label("avsender"));
                item.add(new Label("fritekst"));
            }
        });
    }


}
