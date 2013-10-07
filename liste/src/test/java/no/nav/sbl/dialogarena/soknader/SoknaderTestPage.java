package no.nav.sbl.dialogarena.soknader;

import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;

public class SoknaderTestPage extends WebPage {

    @Inject
    private SoknaderService soknaderService;

    public SoknaderTestPage() {
        add(new SoknadListe("soknadListe", new ListModel<>(soknaderService.getSoknader(""))));
        add(new SoknadListe("soknadListe2", new ListModel<>(soknaderService.getSoknader(""))));
    }

}
