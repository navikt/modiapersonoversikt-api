package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.widget.panels.FeedItemErrorMessagePanel;
import no.nav.sbl.dialogarena.aktorid.service.AktorService;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SoknadListe extends Liste<Soknad> {

    public static final PackageResourceReference SOKNADSLISTE_LESS = new PackageResourceReference(SoknadListe.class, "soknadliste.less");
    private static final IModel<List<Soknad>> MODEL = new ListModel<>(new ArrayList<Soknad>());
    private boolean serviceCallOk;
    @Inject
    private SoknaderService soknaderService;
    @Inject
    private AktorService aktorService;

    public SoknadListe(String id, String fnr) {
        super(id, MODEL);

        serviceCallOk = true;
        List<Soknad> soknader = new ArrayList<>();
        try {
            String aktorId = aktorService.getAktorId(fnr);
            soknader = soknaderService.hentSoknader(aktorId);
        } catch (ApplicationException ex) {
            serviceCallOk = false;
        }
        if (serviceCallOk ) {
            //legger til soknads-liste css-klasse slik at custom css blir brukt.
            //hvis kall til tjeneste feiler ønsker vi ikke å bruke custom-css fordi vi da skal vise en standard feil
            //Hvis det ikke er noen søknader, ikke legg til custom-css
            if(!soknader.isEmpty()){
                this.add(new AttributeAppender("class", new Model<>("soknads-liste"), " "));
            }
        } else {
            //Setter inn en tom søknad slik at newListItem blir kalt en gang
            soknader.clear();
            soknader.add(new Soknad());
        }
        MODEL.setObject(soknader);
    }

    @Override
    public WebMarkupContainer newListItem(String id, IModel<Soknad> model) {
        if (serviceCallOk) {
            return new SoknadItem(id, model);
        } else {
            return new FeedItemErrorMessagePanel(id, new StringResourceModel("sakogbehandling.feil", new Model()));
        }
    }

}
