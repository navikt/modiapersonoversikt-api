package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.*;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.OPPGAVE_OPPRETTET_FERDIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel.OppgaveValg.AVSLUTT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel.OppgaveValg.OPPRETT;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class OppgavePanel extends AnimertPanel {

    public enum OppgaveValg {OPPRETT, AVSLUTT}

    private NyOppgaveFormWrapper nyOppgaveFormWrapper;
    private AvsluttOppgavePanel avsluttOppgavePanel;
    private final OppgaveValgRadioChoice oppgaveValg;
    private final AjaxLink<Void> okKnapp;
    private final AjaxLink<Void> avbrytKnapp;
    private final AjaxLink nyOppgaveKnapp;

    private IModel<Boolean> oppgaveBehandlet = Model.of(false);

    public OppgavePanel(String id, final InnboksVM innboksVM) {
        super(id, true);

        add(new Label("temagruppe", new PropertyModel<String>(innboksVM, "valgtTraad.eldsteMelding.melding.temagruppeNavn")));

        okKnapp = new LukkLink("okKnapp");
        okKnapp.add(visibleIf(oppgaveBehandlet));
        avbrytKnapp = new LukkLink("avbryt");
        avbrytKnapp.add(visibleIf(not(oppgaveBehandlet)));

        final IModel<OppgaveValg> oppgaveValgModel = Model.of(OPPRETT);
        IModel<Boolean> oppgaveKanAvsluttes = new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                String traadId = innboksVM.getSessionHenvendelseId().orElse("");

                boolean valgtTraadHarSessionHenvendelseId = innboksVM.getValgtTraad().getMeldinger().stream()
                        .map(MeldingVM::getTraadId)
                        .anyMatch(traadId::equals);

                int size = innboksVM.tildelteOppgaver == null ? 0 : innboksVM.tildelteOppgaver.size();
                boolean erBehandlet = innboksVM.getValgtTraad().erBehandlet();

                return (valgtTraadHarSessionHenvendelseId && innboksVM.getSessionOppgaveId().isPresent()) ||
                        (size == 1 && erBehandlet);
            }
        };

        oppgaveValg = new OppgaveValgRadioChoice("oppgaveValg", oppgaveValgModel, asList(OppgaveValg.values()));
        add(oppgaveValg.add(visibleIf(both(oppgaveKanAvsluttes).and(not(oppgaveBehandlet)))));

        nyOppgaveFormWrapper = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                avsluttOppgaveBehandling(target);
            }
        };
        nyOppgaveFormWrapper.add(visibleIf(either(isEqualTo(oppgaveValgModel, OPPRETT)).or(not(oppgaveKanAvsluttes))));

        avsluttOppgavePanel = new AvsluttOppgavePanel("avsluttOppgaveForm", innboksVM.getSessionOppgaveId().orElse(null)) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                innboksVM.setSessionOppgaveId(null);
                avsluttOppgaveBehandling(target);
            }
        };
        avsluttOppgavePanel.add(visibleIf(both(isEqualTo(oppgaveValgModel, AVSLUTT)).and(oppgaveKanAvsluttes)));

        nyOppgaveKnapp = new AjaxLink("nyOppgaveKnapp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                oppgaveValgModel.setObject(OPPRETT);
                oppgaveBehandlet.setObject(false);
                nyOppgaveFormWrapper.nullstillSkjema();

                target.add(this, okKnapp, avbrytKnapp, oppgaveValg, nyOppgaveFormWrapper, avsluttOppgavePanel);
            }
        };
        nyOppgaveKnapp.setOutputMarkupPlaceholderTag(true);
        nyOppgaveKnapp.add(visibleIf(oppgaveBehandlet));

        add(nyOppgaveFormWrapper, avsluttOppgavePanel, okKnapp, avbrytKnapp, nyOppgaveKnapp);
    }

    private void avsluttOppgaveBehandling(AjaxRequestTarget target) {
        oppgaveBehandlet.setObject(true);
        target.appendJavaScript("$('#" + okKnapp.getMarkupId() + "').focus();");
        target.add(oppgaveValg, okKnapp, avbrytKnapp, nyOppgaveKnapp);
    }

    private class LukkLink extends AjaxLink<Void> {

        public LukkLink(String id) {
            super(id);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            oppgaveBehandlet.setObject(false);

            nyOppgaveFormWrapper.nullstillSkjema();
            lukkPanel(target);

            send(getPage(), BREADTH, OPPGAVE_OPPRETTET_FERDIG);
        }
    }

    private class OppgaveValgRadioChoice extends RadioChoice<OppgaveValg> {

        public OppgaveValgRadioChoice(String id, IModel<OppgaveValg> model, List<? extends OppgaveValg> choices) {
            super(id, model, choices);
            setOutputMarkupPlaceholderTag(true);

            setChoiceRenderer(new IChoiceRenderer<OppgaveValg>() {
                @Override
                public Object getDisplayValue(OppgaveValg object) {
                    return getString("oppgave.oppgavevalg." + object.name());
                }

                @Override
                public String getIdValue(OppgaveValg object, int index) {
                    return object.name();
                }
            });

            add(new AjaxFormChoiceComponentUpdatingBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.add(nyOppgaveFormWrapper, avsluttOppgavePanel);
                }
            });
        }
    }
}
