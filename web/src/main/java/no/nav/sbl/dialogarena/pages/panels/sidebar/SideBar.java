package no.nav.sbl.dialogarena.pages.panels.sidebar;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class SideBar extends Panel {

	public SideBar(String id, String fnr) {

		super(id);

		add(new VisittkortPanel("visittkortPanel", fnr));
	}
}
