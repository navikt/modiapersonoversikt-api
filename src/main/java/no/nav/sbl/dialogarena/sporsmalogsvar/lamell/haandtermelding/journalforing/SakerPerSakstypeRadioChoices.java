package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import java.util.List;

public class SakerPerSakstypeRadioChoices extends Panel {

    public SakerPerSakstypeRadioChoices(String id, PropertyModel<List<TemaSaker>> model, String sakstypePropertyKey) {
        super(id);

        add(
                new Label("sakstype", new ResourceModel(sakstypePropertyKey)),
                new PropertyListView<TemaSaker>("saksgruppeliste", model) {
                    @Override
                    protected void populateItem(ListItem<TemaSaker> item) {
                        item.add(new Label("temaNavn"));
                        item.add(new PropertyListView<Sak>("saksliste") {
                            @Override
                            protected void populateItem(ListItem<Sak> item) {
                                item.add(new Radio<>("sak", item.getModel()));
                                item.add(new Label("saksId"));
                                item.add(new Label("opprettetDatoFormatert"));
                                item.add(new Label("fagsystemNavn"));
                            }
                        });
                    }
                }
        );
    }

}
