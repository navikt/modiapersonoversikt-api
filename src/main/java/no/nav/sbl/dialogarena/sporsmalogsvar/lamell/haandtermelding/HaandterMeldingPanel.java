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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingPanel extends Panel {

    private final List<AnimertPanel> meldingHaandteringsPaneler = new ArrayList<>();
    private final List<WebMarkupContainer> piler = new ArrayList<>();
    private InnboksVM innboksVM;

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id);
        this.innboksVM = innboksVM;

        add(lagBesvareLink());
        add(lagJournalforEnhet());
        add(lagNyOppgaveEnhet());
        add(lagMerkeEnhet());
    }

    private Component lagBesvareLink() {
        AjaxLink<InnboksVM> besvarLink = new AjaxLink<InnboksVM>("besvar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
            }
        };
        besvarLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.bleInitiertAvBruker()")));
        return besvarLink;
    }
    private Component[] lagJournalforEnhet() {
        return toArray(lagHaandterMeldingEnhet("journalfor", not(new PropertyModel<Boolean>(innboksVM, "valgtTraad.nyesteMelding.nyesteMeldingISinJournalfortgruppe")),
                JournalforingsPanel.class));
    }

    private Component[] lagNyOppgaveEnhet() {
        return toArray(lagHaandterMeldingEnhet("nyoppgave", new PropertyModel<Boolean>(innboksVM, "valgtTraad.erBehandlet()"), NyOppgavePanel.class));
    }

    private Component[] lagMerkeEnhet() {
        return toArray(lagHaandterMeldingEnhet("merke", Model.of(true), MerkePanel.class));
    }

    private static <T extends Component> Component[] toArray(Collection<T> collection) {
        return collection.toArray(new Component[collection.size()]);
    }

    private List<? extends Component> lagHaandterMeldingEnhet(String id, IModel<Boolean> enabled, final Class<? extends AnimertPanel> clazz) {
        AnimertPanel panel;
        try {
            panel = clazz.getConstructor(String.class, InnboksVM.class).newInstance(id + "-panel", innboksVM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        panel.setVisibilityAllowed(false);
        meldingHaandteringsPaneler.add(panel);

        AjaxLink link = new AjaxLink(id + "-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, clazz);
            }
        };
        link.add(enabledIf(enabled));

        WebMarkupContainer pil = new WebMarkupContainer(id + "-pil");
        pil.setOutputMarkupId(true);
        pil.add(hasCssClassIf("opp", new PropertyModel<Boolean>(panel, "visibilityAllowed")));
        pil.add(hasCssClassIf("ned", not(new PropertyModel<Boolean>(panel, "visibilityAllowed"))));
        piler.add(pil);
        return asList(panel, link, pil);
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
