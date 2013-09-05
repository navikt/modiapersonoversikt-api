package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class SideBar extends Panel {

    public SideBar(String id, String fnr, String oppgaveIdFromRequest) {
        super(id);
        VisittkortPanel visittkortPanel = new VisittkortPanel("visittkortPanel", fnr);
        BesvareSporsmalPanel besvarePanel = new BesvareSporsmalPanel("besvarePanel", oppgaveIdFromRequest);
        besvarePanel.setVisible(null != oppgaveIdFromRequest);
        add(visittkortPanel, besvarePanel);
    }

}
