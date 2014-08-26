package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_KONTORSPERRET = "sos.merkepanel.traadkontorsperret";

    @Inject
    private HenvendelseBehandlingService henvendelse;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        final IModel<Boolean> skalOppretteOppgave = Model.of(false);
        final IModel<Boolean> erOppgaveOpprettet = Model.of(false);

        CheckBox opprettOppgaveCheckbox = new CheckBox("opprett-oppgave", skalOppretteOppgave);

        final NyOppgaveFormWrapper nyoppgaveForm = new NyOppgaveFormWrapper("nyoppgave-form", innboksVM){
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                erOppgaveOpprettet.setObject(true);
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

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);

        AjaxLink merkeLink = new AjaxLink("merk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (kanMerkeSomKontorsperret(skalOppretteOppgave.getObject(), erOppgaveOpprettet.getObject())) {
                    henvendelse.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                    innboksVM.oppdaterMeldinger();
                    lukkPanel(target);

                    erOppgaveOpprettet.setObject(false);
                    skalOppretteOppgave.setObject(false);
                } else {
                    error(getString("kontorsperre.oppgave.opprettet.feil"));
                    target.add(feedbackPanel);
                }
            }
        };

        AjaxLink<Void> avbrytLink = new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        };
        add(opprettOppgaveCheckbox, nyoppgaveForm, feedbackPanel, merkeLink, avbrytLink);
    }

    private static boolean kanMerkeSomKontorsperret(boolean skalOppretteOppgave, boolean harOpprettetOppgave) {
        return !skalOppretteOppgave || harOpprettetOppgave;
    }
}
