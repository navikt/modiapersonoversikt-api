package no.nav.sbl.dialogarena.soknader;

import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.soknader.widget.SoknaderWidget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

public class SoknaderTestPage extends WebPage {

    @Inject
    private SoknaderService soknaderService;


    public SoknaderTestPage() {
        add(  /* new SoknaderWidget("soknadWidget", "S", new Model<>("")), */
            new SoknadListe("soknadListe", new ListModel<>(soknaderService.getSoknadListe("")))
        );
    }

}
