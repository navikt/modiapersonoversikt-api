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
        final WebMarkupContainer visForm = new WebMarkupContainer("vis-opprett-oppgave");
        visForm.setOutputMarkupId(true);
        visForm.add(visibleIf(not(erOppgaveOpprettet)));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.add(new AttributeModifier("class", "success"));
        feedbackPanel.setOutputMarkupId(true);

        CheckBox opprettOppgaveCheckbox = new CheckBox("opprett-oppgave", skalOppretteOppgave);

        final NyOppgaveFormWrapper nyoppgaveForm = new NyOppgaveFormWrapper("nyoppgave-form", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                erOppgaveOpprettet.setObject(true);
                info("Oppgave opprettet.");
                target.add(visForm);
                target.add(feedbackPanel);
            }
        };
        nyoppgaveForm.setOutputMarkupPlaceholderTag(true);
        nyoppgaveForm.add(visibleIf(skalOppretteOppgave));

        opprettOppgaveCheckbox.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nyoppgaveForm);
            }
        });
        visForm.add(opprettOppgaveCheckbox, nyoppgaveForm);

        add(visForm, feedbackPanel);
    }

    public boolean kanMerkeSomKontorsperret() {
        return !skalOppretteOppgave.getObject() || erOppgaveOpprettet.getObject();
    }

    public void reset() {
        erOppgaveOpprettet.setObject(false);
        skalOppretteOppgave.setObject(false);
    }
}
