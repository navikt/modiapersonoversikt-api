package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class IsReady extends WebPage {

    public IsReady() {
        this.add(new Component[]{new Label("status", "OK")});
    }
}
