package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.web;

import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.TraadPanel;
import org.apache.wicket.markup.html.WebPage;

public class SporsmalOgSvarPage extends WebPage {

    public SporsmalOgSvarPage() {
        super();
        add(new TraadPanel("besvar", "10108000398"));
    }

}
