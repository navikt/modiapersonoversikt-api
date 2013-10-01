package no.nav.sbl.dialogarena.soknader.panel;

import org.apache.wicket.markup.html.WebPage;

public class SoknaderTestPage extends WebPage {

    public SoknaderTestPage() {
        add(new SoknaderPanel("soknader"));
    }

}
