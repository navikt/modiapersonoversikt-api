package no.nav.sbl.dialogarena.sak.lamell;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static org.apache.wicket.model.Model.of;


public class BehandlingSakerListView extends PropertyListView<String> {
    private SaksoversiktLerret lerret;
    private String fnr;

    public BehandlingSakerListView(String id, List<String> sakstemaer, String fnr, SaksoversiktLerret lerret) {
        super(id, sakstemaer);
        this.lerret = lerret;
        this.fnr = fnr;
    }

    @Override
    protected void populateItem(ListItem<String> item) {
        String sakstema = item.getModelObject();
        item.add(new BehandlingerListView("behandlinger", lerret.getBehandlingerForTema(sakstema), fnr))
            .setOutputMarkupId(true)
            .setMarkupId("behandling_" + sakstema);

        boolean sakstemaErValgt = sakstema.equals(lerret.getAktivtTema().getObject());
        item.add(hasCssClassIf("aktiv", of(sakstemaErValgt)));
    }
}
