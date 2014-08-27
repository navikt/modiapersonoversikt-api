package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class OpprettOppgavePanel extends Panel {

    protected final IModel<Boolean> skalOppretteOppgave = Model.of(false);
    protected final IModel<Boolean> erOppgaveOpprettet = Model.of(false);

    public OpprettOppgavePanel(String id, InnboksVM innboksVM) {
        super(id);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.add(new AttributeModifier("class", "success"));
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer opprettOppgaveWrapper = new WebMarkupContainer("vis-ny-oppgave-wrapper");
        opprettOppgaveWrapper.add(visibleIf(not(erOppgaveOpprettet)));
        opprettOppgaveWrapper.setOutputMarkupId(true);

        final NyOppgaveFormWrapper nyOppgaveForm = new NyOppgaveFormWrapper("nyoppgave-form", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                erOppgaveOpprettet.setObject(true);
                success(getString("oppgave.opprettet.bekreftelse"));

                target.add(opprettOppgaveWrapper, feedbackPanel);
            }
        };
        nyOppgaveForm.add(visibleIf(skalOppretteOppgave));
        nyOppgaveForm.setOutputMarkupPlaceholderTag(true);

        CheckBox opprettOppgaveCheckbox = new AjaxCheckBox("opprett-oppgave-checkbox", skalOppretteOppgave) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nyOppgaveForm);
            }
        };

        opprettOppgaveWrapper.add(opprettOppgaveCheckbox, nyOppgaveForm);
        add(opprettOppgaveWrapper);
    }

    public boolean kanMerkeSomKontorsperret() {
        return !skalOppretteOppgave.getObject() || erOppgaveOpprettet.getObject();
    }

    public void reset() {
        erOppgaveOpprettet.setObject(false);
        skalOppretteOppgave.setObject(false);
    }
}
