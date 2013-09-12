package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.WebPage;

public class SporsmalOgSvarPage extends WebPage {

    public SporsmalOgSvarPage() {
        super();
        add(new BesvareSporsmalPanel("besvar", "1", "28088834986"));
    }

}
