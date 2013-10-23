package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.widget.panels.FeedItemErrorMessagePanel;
import no.nav.sbl.dialogarena.aktorid.service.AktorService;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.apache.wicket.model.Model.of;

public class SoknadListe extends Liste<Soknad> {

    public static final PackageResourceReference SOKNADSLISTE_LESS = new PackageResourceReference(SoknadListe.class, "soknadliste.less");
    private static final IModel<List<Soknad>> model = new ListModel<>(new ArrayList<Soknad>());
    private boolean serviceCallFailed;
    @Inject
    private SoknaderService soknaderService;
    @Inject
    private AktorService aktorService;

    public SoknadListe(String id, String fnr) {
        super(id, model);

        String aktorId = aktorService.getAktorId(fnr);
        List<Soknad> soknader = new ArrayList<>();
        try {
            soknader = soknaderService.getSoknader(aktorId);
        } catch (ApplicationException ex) {
            serviceCallFailed = true;
        }
        if (serviceCallFailed) {
            //Setter inn en tom søknad slik at newListItem blir kalt en gang
            soknader.clear();
            soknader.add(new Soknad());
        }
        setDefaultModel(Model.ofList(soknader));
    }

    @Override
    public WebMarkupContainer newListItem(String id, IModel<Soknad> model) {
        if (serviceCallFailed) {
            return new FeedItemErrorMessagePanel(id, of("Feil ved uthenting av søknader"));
        } else {
            return new SoknadItem(id, model);
        }
    }

}
