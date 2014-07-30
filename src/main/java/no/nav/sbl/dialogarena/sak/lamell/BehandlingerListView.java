package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import java.util.List;

import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.KVITTERING;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;


public class BehandlingerListView extends PropertyListView<GenerellBehandling> {

    private String fnr;

    public BehandlingerListView(String id, List<GenerellBehandling> behandlinger, String fnr) {
        super(id);
        setDefaultModel(ofList(behandlinger));
        this.fnr = fnr;
    }

    @Override
    protected void populateItem(ListItem<GenerellBehandling> item) {
        GenerellBehandling behandling = item.getModelObject();
        if (BEHANDLING.equals(behandling.behandlingsType)) {
            item.add(new BehandlingsPanel("behandling", of(behandling)));
        } else if (KVITTERING.equals(behandling.behandlingsType)) {
            item.add(new KvitteringsPanel("behandling", of((Kvittering)behandling), fnr));
        }
    }
}
