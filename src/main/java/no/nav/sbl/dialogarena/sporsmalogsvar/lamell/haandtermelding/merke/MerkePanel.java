package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave.OpprettOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.FEILSENDT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.KONTORSPERRET;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_MERKET = "sos.merkepanel.merket";

    @Inject
    private HenvendelseBehandlingService henvendelse;

    protected final OpprettOppgavePanel opprettOppgavePanel;

    private Form<MerkVM> merkForm;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        merkForm = new Form<>("merkForm", new CompoundPropertyModel<>(new MerkVM()));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        merkForm.add(feedbackPanel);

        RadioGroup<MerkVM.MerkType> merkRadioGroup = new RadioGroup<>("merkType");
        merkRadioGroup.setRequired(true);
        merkRadioGroup.add(new Radio<>("feilsendtRadio", Model.of(FEILSENDT)));
        merkRadioGroup.add(new Radio<>("kontorsperretRadio", Model.of(KONTORSPERRET)));
        opprettOppgavePanel = new OpprettOppgavePanel("opprettOppgavePanel", innboksVM);
        opprettOppgavePanel.setDefaultModel(this.getDefaultModel());
        merkRadioGroup.add(opprettOppgavePanel);
        merkForm.add(merkRadioGroup);

        AjaxButton merkeLink = new AjaxButton("merk") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                MerkType merkTypeModel = ((MerkVM) form.getModelObject()).getMerkType();
                if (merkTypeModel.equals(KONTORSPERRET)) {
                    haandterKontorsperring(target);
                } else if (merkTypeModel.equals(FEILSENDT)) {
                    haandterFeilsendt(target);
                }
            }

            private void haandterKontorsperring(AjaxRequestTarget target) {
                if (opprettOppgavePanel.kanMerkeSomKontorsperret()) {
                    henvendelse.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                    send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
                    lukkPanel(target);
                } else {
                    error(getString("kontorsperre.oppgave.opprettet.feil"));
                    target.add(feedbackPanel);
                }
            }

            private void haandterFeilsendt(AjaxRequestTarget target) {
                henvendelse.merkSomFeilsendt(innboksVM.getValgtTraad());
                send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
                lukkPanel(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
        merkForm.add(merkeLink);
        add(merkForm);

        AjaxLink<Void> avbrytLink = new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        };
        add(avbrytLink);
    }

    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
        merkForm.setDefaultModelObject(new MerkVM());
        opprettOppgavePanel.reset();
    }

    public Form<MerkVM> getMerkForm() {
        return merkForm;
    }

}
