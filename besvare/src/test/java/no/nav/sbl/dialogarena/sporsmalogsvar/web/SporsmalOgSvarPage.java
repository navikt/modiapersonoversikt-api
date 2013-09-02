package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.SporsmalOgSvarPanel;
import org.apache.wicket.markup.html.WebPage;

public class SporsmalOgSvarPage extends WebPage {

    public SporsmalOgSvarPage() {
        super();
        add(new SporsmalOgSvarPanel("besvar", "28088834986"));
    }
}
