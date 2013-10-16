package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.modia.liste.Liste;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.List;

public class SoknadListe extends Liste<Soknad> {

    public static final PackageResourceReference SOKNADSLISTE_LESS = new PackageResourceReference(SoknadListe.class, "soknadliste.less");

    private boolean serviceCallFailed;

    public SoknadListe(String id, final IModel<List<Soknad>> model, boolean serviceCallFailed) {
        super(id, model);
        this.serviceCallFailed = serviceCallFailed;
        if(serviceCallFailed){
            model.getObject().clear();
            model.getObject().add(new Soknad()); //Setter inn en tom søknad slik at newListItem blir kalt
        }

    }


    @Override
    public WebMarkupContainer newListItem(String id, IModel<Soknad> model) {
        if(serviceCallFailed){
            return new SoknadListeError(id);
        }else{
            return new SoknadItem(id, model);
        }
    }

}
