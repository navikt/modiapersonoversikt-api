package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.OppgavevalgPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class SideBar extends Panel {

    private final BesvareSporsmalPanel besvaresporsmalPanel;
    private final OppgavevalgPanel oppgavevalg;

    public SideBar(String id, String fnr, String oppgaveIdFromRequest) {
        super(id);
        oppgavevalg = new OppgavevalgPanel("oppgavevalg", oppgaveIdFromRequest);
        VisittkortPanel visittkortPanel = new VisittkortPanel("visittkortPanel", fnr);
        besvaresporsmalPanel = new BesvareSporsmalPanel("besvarePanel", oppgaveIdFromRequest, fnr);
        add(visittkortPanel, besvaresporsmalPanel, new HentOppgavePanel("hent-oppgave"), oppgavevalg);
        initVisibility();
    }

    @RunOnEvents(Modus.BESVARE)
    public void besvarmodus() {
        besvaresporsmalPanel.setVisibilityAllowed(true);
        oppgavevalg.setVisibilityAllowed(true);
    }

    public void initVisibility() {
        besvaresporsmalPanel.setVisibilityAllowed(false);
        oppgavevalg.setVisibilityAllowed(false);
    }

}
