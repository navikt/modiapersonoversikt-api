package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import javax.inject.Inject;

import static org.apache.wicket.model.Model.ofList;

public class SakerListView extends PropertyListView<TemaVM> {

    @Inject
    private SaksoversiktService saksoversiktService;

    public SakerListView(String id, String fnr) {
        super(id);
        setDefaultModel(ofList(saksoversiktService.hentTemaer(fnr)));
    }

    @Override
    protected void populateItem(ListItem<TemaVM> item) {
        String temanavn = item.getModelObject().temakode;
        String datoStreng = item.getModelObject().sistoppdaterteBehandling.behandlingDato.toString();
        item.add(
                new Label("sakstema", temanavn),
                new Label("dato", datoStreng)
        );
    }
}
