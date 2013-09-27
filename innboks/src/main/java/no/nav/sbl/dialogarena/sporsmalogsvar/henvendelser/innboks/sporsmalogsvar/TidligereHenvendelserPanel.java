package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.sporsmalogsvar;


import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.HenvendelseVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<HenvendelseVM>("tidligereHenvendelser") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {
                item.add(new Label("opprettetDato"));
                item.add(new Label("henvendelse.overskrift"));
                item.add(new Label("henvendelse.fritekst"));
            }
        });
    }


}
