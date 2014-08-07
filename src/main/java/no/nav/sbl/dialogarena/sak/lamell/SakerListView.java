package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import javax.inject.Inject;

import static org.apache.wicket.model.Model.ofList;

public class SakerListView extends PropertyListView<TemaVM> {

    @Inject
    private SaksoversiktService saksoversiktService;

    private final SaksoversiktLerret lerret;

    public SakerListView(String id, String fnr, SaksoversiktLerret lerret) {
        super(id);
        this.lerret = lerret;
        setDefaultModel(ofList(saksoversiktService.hentTemaer(fnr)));
    }

    @Override
    protected void populateItem(ListItem<TemaVM> item) {
        String sakstema = item.getModelObject().temakode;
        String datoStreng = item.getModelObject().sistoppdaterteBehandling.behandlingDato.toString();
        item.add(
                new Superlenke("temalenke", sakstema, datoStreng)
        );
    }

    private class Superlenke extends AjaxLink {

        private String sakstema;

        public Superlenke(String id, String sakstema, String datoStreng) {
            super(id);
            this.sakstema = sakstema;
            add(
                    new Label("sakstema", sakstema),
                    new Label("dato", datoStreng)
            );
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            target.add(lerret);
            lerret.hentNyeHendelser(sakstema);
        }
    }
}
