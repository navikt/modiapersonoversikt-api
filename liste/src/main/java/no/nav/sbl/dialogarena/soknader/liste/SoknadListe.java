package no.nav.sbl.dialogarena.soknader.liste;

import no.nav.modig.modia.liste.Liste;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.List;

public class SoknadListe extends Liste<Soknad> {
    public static final CssResourceReference CSS_RESOURCE = new CssResourceReference(SoknadListe.class, "soknadliste.css");

    public SoknadListe(String id, final IModel<List<Soknad>> model) {
        super(id, model);
    }

    @Override
    public WebMarkupContainer newListItem(String id, IModel<Soknad> model) {
        return new SoknadItem(id, model);
    }
}
