package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.TraadPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BesvarePanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(OppgavePanel.class);

    private final TraadPanel besvaresporsmalPanel;
	private final WebMarkupContainer referatPlaceholder;

    public BesvarePanel(String id, String fnr) {

        super(id);

        besvaresporsmalPanel = new TraadPanel("besvarePanel", fnr);
	    besvaresporsmalPanel.setVisibilityAllowed(false);

	    referatPlaceholder = new WebMarkupContainer("referatPlaceholder");
		referatPlaceholder.setVisibilityAllowed(true).setOutputMarkupId(true);

        add(besvaresporsmalPanel, referatPlaceholder);
    }

    @RunOnEvents(Modus.BESVARE)
    public void besvarmodus(AjaxRequestTarget target, String oppgaveId) {
        LOG.info("Modus: {}. Oppgave: {}", Modus.BESVARE, oppgaveId);

        besvaresporsmalPanel.besvar(oppgaveId);
        besvaresporsmalPanel.setVisibilityAllowed(true);
	    referatPlaceholder.setVisibilityAllowed(false);

        if (target != null) {
            target.add(besvaresporsmalPanel, referatPlaceholder);
        }
    }
}
