package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar;

import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave.HentOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.OppgavevalgPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.BesvareSporsmalPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SideBar extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(SideBar.class);

    private final BesvareSporsmalPanel besvaresporsmalPanel;
    private final OppgavevalgPanel oppgavevalg;
    private final HentOppgavePanel hentOppgavePanel;

    public SideBar(String id, String fnr) {
        super(id);
        oppgavevalg = new OppgavevalgPanel("oppgavevalg");
        besvaresporsmalPanel = new BesvareSporsmalPanel("besvarePanel", fnr);
        hentOppgavePanel = new HentOppgavePanel("hent-oppgave");
        initVisibility();
        add(
                hentOppgavePanel,
                new VisittkortPanel("visittkortPanel", fnr),
                besvaresporsmalPanel,
                oppgavevalg
        );
    }

    @RunOnEvents(Modus.BESVARE)
    public void besvarmodus(AjaxRequestTarget target, String oppgaveId) {
        LOG.info("Modus: {}. Oppgave: {}", Modus.BESVARE, oppgaveId);

        besvaresporsmalPanel.besvar(oppgaveId);
        besvaresporsmalPanel.setVisibilityAllowed(true);
        oppgavevalg.setVisibilityAllowed(true);
        hentOppgavePanel.setVisibilityAllowed(false);
        if (target != null) {
            target.add(besvaresporsmalPanel, oppgavevalg);
        }
    }

    @RunOnEvents(Modus.KVITTERING)
    public void kvitteringsmodus(AjaxRequestTarget target) {
        hentOppgavePanel.setVisibilityAllowed(true);
        if (target != null) {
            target.add(hentOppgavePanel);
        }
    }

    public final void initVisibility() {
        besvaresporsmalPanel.setVisibilityAllowed(false);
        oppgavevalg.setVisibilityAllowed(false);
        hentOppgavePanel.setVisibilityAllowed(true);
    }

}
