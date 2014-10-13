package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave;

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

public class OpprettOppgavePanel extends Panel {

    public static final String OPPGAVE_OPPRETTET = "sos.oppgave.opprettet";

    protected final IModel<Boolean> skalOppretteOppgave = Model.of(false);
    protected final IModel<Boolean> erOppgaveOpprettet = Model.of(false);

    private CheckBox opprettOppgaveCheckbox;
    private final NyOppgaveFormWrapper nyOppgaveForm;

    public OpprettOppgavePanel(String id, InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer opprettOppgaveWrapper = new WebMarkupContainer("visNyOppgaveWrapper");
        opprettOppgaveWrapper.setOutputMarkupId(true);

        nyOppgaveForm = new NyOppgaveFormWrapper("nyoppgaveForm", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                erOppgaveOpprettet.setObject(true);
                send(getPage(), Broadcast.DEPTH, OPPGAVE_OPPRETTET);
                target.add(opprettOppgaveWrapper);
            }
        };
        nyOppgaveForm.add(visibleIf(skalOppretteOppgave));
        nyOppgaveForm.setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer opprettOppgaveCheckboxWrapper = new WebMarkupContainer("opprettOppgaveCheckboxWrapper");

        opprettOppgaveCheckbox = new AjaxCheckBox("opprettOppgaveCheckbox", skalOppretteOppgave) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nyOppgaveForm);
            }
        };
        opprettOppgaveCheckboxWrapper.add(opprettOppgaveCheckbox);
        opprettOppgaveCheckboxWrapper.add(visibleIf(not(erOppgaveOpprettet)));


        opprettOppgaveWrapper.add(opprettOppgaveCheckboxWrapper, nyOppgaveForm);
        add(opprettOppgaveWrapper);
    }

    public boolean kanMerkeSomKontorsperret() {
        return !skalOppretteOppgave.getObject() || erOppgaveOpprettet.getObject();
    }

    public void tvingFremMarkupOppdateringAvCheckBox() {
        opprettOppgaveCheckbox.modelChanged();
    }

    public void reset() {
        erOppgaveOpprettet.setObject(false);
        skalOppretteOppgave.setObject(false);
        nyOppgaveForm.nullstillSkjema();
    }

}
