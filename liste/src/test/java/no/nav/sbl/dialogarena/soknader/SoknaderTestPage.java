package no.nav.sbl.dialogarena.soknader;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SoknaderTestPage extends WebPage {

    @Inject
    private SoknaderService soknaderService;

    public SoknaderTestPage() {
        boolean serviceCallFailed = false;
        List<Soknad> soknader = new ArrayList<>();

        try {
            soknader = soknaderService.getSoknader("");
        } catch (ApplicationException ex) {
            serviceCallFailed = true;
        }
        add(new SoknadListe("soknadListe", new ListModel<>(soknader), serviceCallFailed));
        add(new SoknadListe("soknadListe2", new ListModel<>(soknader), serviceCallFailed));

    }

}
