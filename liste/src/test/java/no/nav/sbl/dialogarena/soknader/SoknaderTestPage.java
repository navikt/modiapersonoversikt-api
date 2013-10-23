package no.nav.sbl.dialogarena.soknader;

import no.nav.sbl.dialogarena.soknader.liste.SoknadListe;
import org.apache.wicket.markup.html.WebPage;

public class SoknaderTestPage extends WebPage {

    public SoknaderTestPage() {
        add(new SoknadListe("soknadListe", "06047848871"));
        add(new SoknadListe("soknadListe2", "23054549733"));
    }

}
