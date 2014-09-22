package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static org.apache.wicket.model.Model.of;


public class BehandlingSakerListView extends PropertyListView<TemaVM> {
    private SaksoversiktLerret lerret;
    private String fnr;

    public BehandlingSakerListView(String id, List<TemaVM> sakstemaer, String fnr, SaksoversiktLerret lerret) {
        super(id, sakstemaer);
        this.lerret = lerret;
        this.fnr = fnr;
    }

    @Override
    protected void populateItem(ListItem<TemaVM> item) {
        TemaVM sakstema = item.getModelObject();
        item.add(new BehandlingerListView("behandlinger", lerret.getBehandlingerForTema(sakstema), fnr))
            .setOutputMarkupId(true)
            .setMarkupId("behandling_" + sakstema.temakode);

        boolean sakstemaErValgt = sakstema.temakode.equals(lerret.getAktivtTema().getObject());
        item.add(hasCssClassIf("aktiv", of(sakstemaErValgt)));
    }
}
