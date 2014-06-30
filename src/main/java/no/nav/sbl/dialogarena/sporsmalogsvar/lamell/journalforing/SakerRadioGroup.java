package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class SakerRadioGroup extends RadioGroup<Sak> {

    public SakerRadioGroup(String id, SakerVM sakerVM) {
        super(id);
        setRequired(true);

        add(new PropertyListView<TemaMedSaker>("saksgruppeliste", new PropertyModel<List<TemaMedSaker>>(sakerVM, "saksgruppeliste")) {
            @Override
            protected void populateItem(ListItem<TemaMedSaker> item) {
                TemaMedSaker temaMedSaker = item.getModelObject();
                item.add(new Label("tema"));
                item.add(new PropertyListView<Sak>("saker", new PropertyModel<List<Sak>>(temaMedSaker, "saksliste")) {
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
