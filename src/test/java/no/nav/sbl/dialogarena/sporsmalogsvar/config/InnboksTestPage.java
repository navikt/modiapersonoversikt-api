package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks;
import org.apache.wicket.markup.html.WebPage;

public class InnboksTestPage extends WebPage {
    public InnboksTestPage() {
        add(new Innboks("innboks", "11111111111"));
    }
}
