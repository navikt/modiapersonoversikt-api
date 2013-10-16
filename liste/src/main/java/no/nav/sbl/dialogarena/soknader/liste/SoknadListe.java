package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.modia.liste.Liste;
import no.nav.modig.modia.widget.panels.FeedItemErrorMessagePanel;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

public class SoknadListe extends Liste<Soknad> {

    public static final PackageResourceReference SOKNADSLISTE_LESS = new PackageResourceReference(SoknadListe.class, "soknadliste.less");

    private boolean serviceCallFailed;

    public SoknadListe(String id, final IModel<List<Soknad>> model, boolean serviceCallFailed) {
        super(id, model);
        this.serviceCallFailed = serviceCallFailed;
        if(serviceCallFailed){
            //Setter inn en tom søknad slik at newListItem blir kalt en gang
            model.getObject().clear();
            model.getObject().add(new Soknad());
        }

    }


    @Override
    public WebMarkupContainer newListItem(String id, IModel<Soknad> model) {
        if(serviceCallFailed){
            return new FeedItemErrorMessagePanel(id, Model.of("Feil ved uthenting av søknader"));
        }else{
            return new SoknadItem(id, model);
        }
    }

}
