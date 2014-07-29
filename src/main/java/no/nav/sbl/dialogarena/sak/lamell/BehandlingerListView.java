package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import java.util.List;
import java.util.Locale;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.KVITTERING;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;


public class BehandlingerListView extends PropertyListView<GenerellBehandling> {

    public BehandlingerListView(String id, List<GenerellBehandling> behandlinger) {
        super(id);
        setDefaultModel(ofList(behandlinger));
    }

    @Override
    protected void populateItem(ListItem<GenerellBehandling> item) {
        GenerellBehandling behandling = item.getModelObject();
        if (BEHANDLING.equals(behandling.behandlingsType)) {
            item.add(new BehandlingsPanel("behandling", of(behandling)));
        } else if (KVITTERING.equals(behandling.behandlingsType)) {
            item.add(new KvitteringsPanel("behandling", of(behandling)));
        }
        item.add(
                new Label("hardkodettittel", "Hardkodet tittel"),
                new Label("behandling-dato-dag", item.getModelObject().behandlingDato.getDayOfMonth()),
                new Label("behandling-dato-maaned", item.getModelObject().behandlingDato.toString("MMM", new Locale("no")))
        );
    }
}
