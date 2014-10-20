package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class NyOppgavePanel extends AnimertPanel {

    private NyOppgaveFormWrapper nyOppgaveFormWrapper;

    public NyOppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);

        add(new Label("temagruppe", new StringResourceModel("${temagruppeKey}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding"))));

        final AjaxLink<Void> okKnapp = new LukkLink("okKnapp");
        final AjaxLink<Void> avbrytKnapp = new LukkLink("avbryt");

        nyOppgaveFormWrapper = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                oppgaveOpprettet.setObject(true);
                target.appendJavaScript("$('#"+okKnapp.getMarkupId()+"').focus();");
                target.add(okKnapp, avbrytKnapp);
            }
        };

        okKnapp.add(visibleIf(nyOppgaveFormWrapper.oppgaveOpprettet));
        avbrytKnapp.add(visibleIf(not(nyOppgaveFormWrapper.oppgaveOpprettet)));

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
            lukkPanel(target);
        }
    }
}
