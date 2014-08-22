package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave.NyOppgavePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingPanel extends Panel {

    private final List<AnimertPanel> meldingHaandteringsPaneler;
    private List<WebMarkupContainer> piler = new ArrayList<>();

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id);

        final JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforingsPanel", innboksVM);
        journalforingsPanel.setVisibilityAllowed(false);
        final NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyOppgavePanel", innboksVM);
        nyOppgavePanel.setVisibilityAllowed(false);
        final MerkePanel merkePanel = new MerkePanel("merkePanel", innboksVM);
        merkePanel.setVisibilityAllowed(false);

        meldingHaandteringsPaneler = asList(journalforingsPanel, nyOppgavePanel, merkePanel);

        AjaxLink<InnboksVM> besvarLink = new AjaxLink<InnboksVM>("besvar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
            }
        };
        besvarLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.bleInitiertAvBruker()")));

        AjaxLink journalforLink = new AjaxLink("journalfor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, JournalforingsPanel.class);
            }
        };
        journalforLink.add(enabledIf(not(new PropertyModel<Boolean>(innboksVM, "valgtTraad.nyesteMelding.nyesteMeldingISinJournalfortgruppe"))));
        WebMarkupContainer journalforPil = new WebMarkupContainer("journalfor-pil");
        journalforPil.setOutputMarkupId(true);
        journalforPil.add(hasCssClassIf("opp", new PropertyModel<Boolean>(journalforingsPanel, "visibilityAllowed")));
        journalforPil.add(hasCssClassIf("ned", not(new PropertyModel<Boolean>(journalforingsPanel, "visibilityAllowed"))));
        piler.add(journalforPil);

        AjaxLink nyOppgaveLink = new AjaxLink("nyoppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, NyOppgavePanel.class);
            }
        };
        nyOppgaveLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.erBehandlet()")));
        WebMarkupContainer nyOppgavePil = new WebMarkupContainer("ny-oppgave-pil");
        nyOppgavePil.setOutputMarkupId(true);
        nyOppgavePil.add(hasCssClassIf("opp", new PropertyModel<Boolean>(nyOppgavePanel, "visibilityAllowed")));
        nyOppgavePil.add(hasCssClassIf("ned", not(new PropertyModel<Boolean>(nyOppgavePanel, "visibilityAllowed"))));
        piler.add(nyOppgavePil);

        AjaxLink merkeLink = new AjaxLink("merke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, MerkePanel.class);
            }
        };
        WebMarkupContainer merkePil = new WebMarkupContainer("merke-pil");
        merkePil.setOutputMarkupId(true);
        merkePil.add(hasCssClassIf("opp", new PropertyModel<Boolean>(merkePanel, "visibilityAllowed")));
        merkePil.add(hasCssClassIf("ned", not(new PropertyModel<Boolean>(merkePanel, "visibilityAllowed"))));
        piler.add(merkePil);

        add(besvarLink, journalforLink, nyOppgaveLink, merkeLink, journalforPil, nyOppgavePil, merkePil,
                journalforingsPanel, nyOppgavePanel, merkePanel);
    }

    private void togglePaneler(AjaxRequestTarget target, Class synligPanelType) {
        for (AnimertPanel panel : meldingHaandteringsPaneler) {
            if (panel.getClass() == synligPanelType) {
                panel.togglePanel(target);
            } else {
                panel.lukkPanel(target);
            }
        }
        target.add(piler.toArray(new Component[piler.size()]));
    }
}
