package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class SakerPerSakstypeRadioChoices extends Panel {

    public SakerPerSakstypeRadioChoices(String id, PropertyModel model, String sakstypePropertyKey) {
        super(id);

        add(
                new Label("sakstype", getString(sakstypePropertyKey)),
                new PropertyListView<TemaMedSaker>("saksgruppeliste", model) {
                    @Override
                    protected void populateItem(ListItem<TemaMedSaker> item) {
                        item.add(new Label("tema"));
                        item.add(new PropertyListView<Sak>("saksliste") {
                            @Override
                            protected void populateItem(ListItem<Sak> item) {
                                item.add(new Radio<>("sak", item.getModel()));
                                item.add(new Label("saksId"));
                                item.add(new Label("opprettetDato", Datoformat.kort(item.getModelObject().opprettetDato)));
                                item.add(new Label("fagsystem"));
                            }
                        });
                    }
                });
    }
}
