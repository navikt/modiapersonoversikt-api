package no.nav.sbl.dialogarena.modiabrukerdialog.web.tempnaisgosys;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

public class GosysNaisLenke extends Panel {
    public GosysNaisLenke(String id, String fnr) {
        super(id);
        String url = hentUrl() + "/personoversikt/fnr=" + fnr;
        add(new ExternalLink("naisGosys", url));
    }

    private String hentUrl() {
        String url = System.getProperty("gosys.nais.url");
        return url == null ? "https://gosys-nais-q1.nais.preprod.local/gosys" : url;
    }
}
