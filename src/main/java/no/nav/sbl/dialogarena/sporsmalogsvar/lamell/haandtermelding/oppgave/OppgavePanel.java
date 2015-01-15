package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel.OppgaveValg.AVSLUTT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel.OppgaveValg.OPPRETT;

public class OppgavePanel extends AnimertPanel {

    public static enum OppgaveValg {OPPRETT, AVSLUTT}

    private NyOppgaveFormWrapper nyOppgaveFormWrapper;
    private AvsluttOppgavePanel avsluttOppgavePanel;

    public OppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);

        add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding"))));

        final AjaxLink<Void> okKnapp = new LukkLink("okKnapp");
        final AjaxLink<Void> avbrytKnapp = new LukkLink("avbryt");

        IModel<OppgaveValg> oppgaveValgModel = Model.of(OPPRETT);

        add(new OppgaveValgRadioChoice("oppgaveValg", oppgaveValgModel, asList(OppgaveValg.values())));

        nyOppgaveFormWrapper = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                oppgaveOpprettet.setObject(true);
                target.appendJavaScript("$('#" + okKnapp.getMarkupId() + "').focus();");
                target.add(okKnapp, avbrytKnapp);
            }
        };
        nyOppgaveFormWrapper.add(visibleIf(isEqualTo(oppgaveValgModel, OPPRETT)));

        avsluttOppgavePanel = new AvsluttOppgavePanel("avsluttOppgaveForm");
        avsluttOppgavePanel.add(visibleIf(isEqualTo(oppgaveValgModel, AVSLUTT)));

        okKnapp.add(visibleIf(nyOppgaveFormWrapper.oppgaveOpprettet));
        avbrytKnapp.add(visibleIf(not(nyOppgaveFormWrapper.oppgaveOpprettet)));

        add(nyOppgaveFormWrapper, avsluttOppgavePanel, okKnapp, avbrytKnapp);
    }

    private class LukkLink extends AjaxLink<Void> {

        public LukkLink(String id) {
            super(id);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            nyOppgaveFormWrapper.nullstillSkjema();
            lukkPanel(target);
        }
    }

    private class OppgaveValgRadioChoice extends RadioChoice<OppgaveValg> {

        public OppgaveValgRadioChoice(String id, IModel<OppgaveValg> model, List<? extends OppgaveValg> choices) {
            super(id, model, choices);

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
