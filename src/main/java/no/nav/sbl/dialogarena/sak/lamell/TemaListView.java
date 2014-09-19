package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.util.ArrayList;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.sak.util.SakDateFormatter.printLongDate;
import static org.apache.wicket.model.Model.of;
import static org.apache.wicket.model.Model.ofList;
import static org.slf4j.LoggerFactory.getLogger;

public class TemaListView extends PropertyListView<TemaVM> {

    private Logger logger = getLogger(TemaListView.class);

    @Inject
    private SaksoversiktService saksoversiktService;

    @Inject
    private BulletProofKodeverkService kodeverk;

    private final SaksoversiktLerret lerret;

    public TemaListView(String id, String fnr, SaksoversiktLerret lerret) {
        super(id);
        this.lerret = lerret;
        try {
            setDefaultModel(ofList(saksoversiktService.hentTemaer(fnr)));
        } catch (SystemException e) {
            //Feilmelding vises i lamellen dersom denne feilen oppstår
            logger.error("Feil ved kall til baksystem", e);
            setDefaultModel(ofList(new ArrayList<>()));
        }
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
        public Temalenke(String id, String sakstema, String datoStreng) {
            super(id, "#"+sakstema);
            add(
                    new Label("sakstema", kodeverk.getTemanavnForTemakode(sakstema, ARKIVTEMA)),
                    new Label("dato", datoStreng)
            );
        }
    }
}
