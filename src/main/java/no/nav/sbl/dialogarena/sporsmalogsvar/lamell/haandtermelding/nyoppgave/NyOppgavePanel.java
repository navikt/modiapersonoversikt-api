package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class NyOppgavePanel extends AnimertPanel {

    private NyOppgaveFormWrapper nyOppgaveFormWrapper;

    private final IModel<Boolean> oppgaveOpprettet = Model.of(false);

    public NyOppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);

        final AjaxLink<Void> okKnapp = new LukkLink("okKnapp");
        okKnapp.add(visibleIf(oppgaveOpprettet));
        final AjaxLink<Void> avbrytKnapp = new LukkLink("avbryt");
        avbrytKnapp.add(visibleIf(not(oppgaveOpprettet)));

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        nyOppgaveFormWrapper = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                oppgaveOpprettet.setObject(true);
                target.add(okKnapp, avbrytKnapp);
            }
        };

        add(nyOppgaveFormWrapper, okKnapp, avbrytKnapp);
    }

    private class LukkLink extends AjaxLink<Void> {

        public LukkLink(String id) {
            super(id);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            nyOppgaveFormWrapper.nullstillSkjema();
            oppgaveOpprettet.setObject(false);
            lukkPanel(target);
        }
    }
}
