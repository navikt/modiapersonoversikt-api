package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class KontorsperrePanel extends Panel {

    public static final String OPPGAVE_OPPRETTET = "sos.oppgave.opprettet";
    public static final String OPPRETT_OPPGAVE_TOGGLET = "sos.oppgave.skalopprette";

    public final IModel<Boolean> skalOppretteOppgave = Model.of(true);

    private final NyOppgaveFormWrapper nyOppgaveForm;
    private final CheckBox opprettOppgaveCheckbox;

    public KontorsperrePanel(String id, InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer opprettOppgaveCheckboxWrapper = new WebMarkupContainer("opprettOppgaveCheckboxWrapper");
        opprettOppgaveCheckboxWrapper.setOutputMarkupId(true);

        nyOppgaveForm = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                oppgaveOpprettet.setObject(true);
                send(getPage(), Broadcast.DEPTH, OPPGAVE_OPPRETTET);
                target.add(opprettOppgaveCheckboxWrapper);
            }
        };
        nyOppgaveForm.add(visibleIf(skalOppretteOppgave));
        nyOppgaveForm.setOutputMarkupPlaceholderTag(true);

        opprettOppgaveCheckbox = new AjaxCheckBox("opprettOppgaveCheckbox", skalOppretteOppgave) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                nyOppgaveForm.nullstillSkjema();
                send(this, Broadcast.BUBBLE, OPPRETT_OPPGAVE_TOGGLET);
                target.add(nyOppgaveForm);
            }
        };
        opprettOppgaveCheckboxWrapper.add(opprettOppgaveCheckbox);
        opprettOppgaveCheckboxWrapper.add(visibleIf(not(nyOppgaveForm.oppgaveOpprettet)));

        add(opprettOppgaveCheckboxWrapper, nyOppgaveForm);
    }

    public boolean kanMerkeSomKontorsperret() {
        return !skalOppretteOppgave.getObject() || nyOppgaveForm.oppgaveOpprettet.getObject();
    }

    public void reset() {
        skalOppretteOppgave.setObject(false);
        nyOppgaveForm.nullstillSkjema();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        opprettOppgaveCheckbox.modelChanged();
    }

}
