package no.nav.sbl.dialogarena.soknader.panel;

import no.nav.sbl.dialogarena.soknader.widget.SoknaderWidget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;

public class SoknaderTestPage extends WebPage {

    public SoknaderTestPage() {
        add(new SoknaderPanel("soknader"),
            new SoknaderWidget("soknadWidget", "S", new Model<>("")));
    }

}
