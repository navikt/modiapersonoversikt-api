package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sak.util.SakDateFormatter.printLongDate;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;

public class TemaListView extends PropertyListView<TemaVM> {

    @Inject
    private SaksoversiktService saksoversiktService;

    private final SaksoversiktLerret lerret;

    public TemaListView(String id, String fnr, SaksoversiktLerret lerret) {
        super(id);
        this.lerret = lerret;
        setDefaultModel(ofList(saksoversiktService.hentTemaer(fnr)));
    }

    @Override
    protected void populateItem(ListItem<TemaVM> item) {
        String sakstema = item.getModelObject().temakode;
        String datoStreng = printLongDate(item.getModelObject().sistoppdaterteBehandling.behandlingDato);
        boolean sakstemaErValgt = sakstema.equals(lerret.getAktivtTema().getObject());

        item.add(new Temalenke("temalenke", sakstema, datoStreng));
        item.add(hasCssClassIf("aktiv", of(sakstemaErValgt)));
    }

    private class Temalenke extends AjaxLink {

        private String sakstema;

        public Temalenke(String id, String sakstema, String datoStreng) {
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
