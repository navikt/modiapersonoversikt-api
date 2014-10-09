package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.sak.util.SakDateFormatter.printLongDate;
import static org.apache.wicket.model.Model.of;

public class TemaListView extends PropertyListView<TemaVM> {

    @Inject
    private BulletProofKodeverkService kodeverk;

    private final SaksoversiktLerret lerret;

    public TemaListView(String id, List<TemaVM> temaer, SaksoversiktLerret lerret) {
        super(id, temaer);
        this.lerret = lerret;
    }

    @Override
    protected void populateItem(ListItem<TemaVM> item) {
        String sakstema = item.getModelObject().temakode;
        String datoStreng = printLongDate(item.getModelObject().sistoppdaterteBehandling.behandlingDato);
        boolean sakstemaErValgt = sakstema.equals(lerret.getAktivtTema().getObject());

        item.add(new Temalenke("temalenke", sakstema, datoStreng));
        item.add(hasCssClassIf("aktiv", of(sakstemaErValgt)));
    }

    private class Temalenke extends ExternalLink {
        public Temalenke(String id, final String sakstema, String datoStreng) {
            super(id, "#" + sakstema);
            add(
                    new Label("sakstema", kodeverk.getTemanavnForTemakode(sakstema, ARKIVTEMA)),
                    new Label("dato", datoStreng)
            );

            add(new AjaxEventBehavior("updateaktivttema") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    lerret.settAktivtTema(sakstema);
                }
            });
        }
    }
}
