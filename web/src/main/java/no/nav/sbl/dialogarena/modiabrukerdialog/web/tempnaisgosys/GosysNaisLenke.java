package no.nav.sbl.dialogarena.modiabrukerdialog.web.tempnaisgosys;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

public class GosysNaisLenke extends Panel {
    public GosysNaisLenke(String id) {
        super(id);
        add(new ExternalLink("naisGosys", hentUrl()));
    }

    private String hentUrl() {
        String url = System.getProperty("gosys.nais.url");
        return url == null ? "https://gosys-nais-q1.nais.preprod.local/gosys" : url;
    }
}
