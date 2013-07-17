package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
//import no.nav.sbl.dialogarena.besvare.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class SideBar extends Panel {

    public SideBar(String id, String fnr) {
        super(id);
        add(new VisittkortPanel("visittkortPanel", fnr));
//        add(new BesvareSporsmalPanel("besvarePanel"));
    }

}
