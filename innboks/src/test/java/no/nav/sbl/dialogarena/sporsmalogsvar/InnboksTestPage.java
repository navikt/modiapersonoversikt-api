package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.Innboks;
import org.apache.wicket.markup.html.WebPage;

public class InnboksTestPage extends WebPage {
    public InnboksTestPage() {
        add(new Innboks("innboks"));
    }
}
