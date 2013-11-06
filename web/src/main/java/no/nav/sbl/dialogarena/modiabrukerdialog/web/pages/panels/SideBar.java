package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class SideBar extends Panel {

    public SideBar(String id) {
        super(id);
        add(new WebMarkupContainer("referatPlaceholder"));
    }
}