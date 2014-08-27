package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class OpprettOppgave extends Panel {

    final IModel<Boolean> skalOppretteOppgave = Model.of(false);
    final IModel<Boolean> erOppgaveOpprettet = Model.of(false);

    public OpprettOppgave(String id, InnboksVM innboksVM) {
        super(id);

        final WebMarkupContainer visNyOppgaveForm = new WebMarkupContainer("vis-ny-oppgave-wrapper");
        visNyOppgaveForm.add(visibleIf(not(erOppgaveOpprettet)));
        visNyOppgaveForm.setOutputMarkupId(true);

        CheckBox opprettOppgaveCheckbox = new CheckBox("opprett-oppgave-checkbox", skalOppretteOppgave);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);


        final NyOppgaveFormWrapper nyOppgaveForm = new NyOppgaveFormWrapper("nyoppgave-form", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                erOppgaveOpprettet.setObject(true);

                info("Oppgave opprettet.");
                feedbackPanel.add(new AttributeModifier("class", "success"));

                target.add(visNyOppgaveForm, feedbackPanel);
            }
        };
        nyOppgaveForm.add(visibleIf(skalOppretteOppgave));
        nyOppgaveForm.setOutputMarkupPlaceholderTag(true);

        opprettOppgaveCheckbox.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nyOppgaveForm);
            }
        });

        visNyOppgaveForm.add(opprettOppgaveCheckbox, nyOppgaveForm);
        add(visNyOppgaveForm, feedbackPanel);
    }

    public boolean kanMerkeSomKontorsperret() {
        return !skalOppretteOppgave.getObject() || erOppgaveOpprettet.getObject();
    }

    public void reset() {
        erOppgaveOpprettet.setObject(false);
        skalOppretteOppgave.setObject(false);
    }
}
