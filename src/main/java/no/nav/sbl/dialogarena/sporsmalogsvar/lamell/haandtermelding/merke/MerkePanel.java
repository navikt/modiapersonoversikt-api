package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave.OpprettOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.FEILSENDT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.KONTORSPERRET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave.OpprettOppgavePanel.OPPGAVE_OPPRETTET;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_MERKET = "sos.merkepanel.merket";

    @Inject
    private HenvendelseBehandlingService henvendelseService;

    private final OpprettOppgavePanel opprettOppgavePanel;
    private final FeedbackPanel feedbackPanel;
    private final CompoundPropertyModel<MerkVM> merkVMModel;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        merkVMModel = new CompoundPropertyModel<>(new MerkVM());
        final Form<MerkVM> merkForm = new Form<>("merkForm", merkVMModel);

        final RadioGroup<MerkType> merkRadioGroup = new RadioGroup<>("merkType");

        feedbackPanel = new FeedbackPanel("feedbackMerkPanel", new ComponentFeedbackMessageFilter(merkRadioGroup));
        feedbackPanel.setOutputMarkupId(true);
        merkForm.add(feedbackPanel);

        merkRadioGroup.setRequired(true);
        merkRadioGroup.add(new Radio<>("feilsendtRadio", Model.of(FEILSENDT)));
        merkRadioGroup.add(new Radio<>("kontorsperretRadio", Model.of(KONTORSPERRET)));
        opprettOppgavePanel = new OpprettOppgavePanel("opprettOppgavePanel", innboksVM);
        opprettOppgavePanel.setDefaultModel(this.getDefaultModel());
        opprettOppgavePanel.add(visibleIf(new PropertyModel<Boolean>(merkVMModel, "kontorsperret")));
        merkRadioGroup.add(opprettOppgavePanel);
        merkRadioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(opprettOppgavePanel);
                refreshFeedbackPanel(target);
            }
        });
        merkForm.add(merkRadioGroup);
        merkForm.add(createAjaxSubmitLink(innboksVM, merkRadioGroup));
        add(merkForm);
        add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });
    }

    private AjaxButton createAjaxSubmitLink(final InnboksVM innboksVM, final RadioGroup<MerkType> merkRadioGroup) {
        return new AjaxButton("merk") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (merkVMModel.getObject().isKontorsperret()) {
                    haandterKontorsperring(target, form);
                } else {
                    haandterFeilsendt(target);
                }
            }

            private void haandterKontorsperring(AjaxRequestTarget target, Form<?> form) {
                if (opprettOppgavePanel.kanMerkeSomKontorsperret()) {
                    henvendelseService.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                    send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
                    lukkPanel(target);
                } else {
                    onError(target, form);
                }
            }

            private void haandterFeilsendt(AjaxRequestTarget target) {
                henvendelseService.merkSomFeilsendt(innboksVM.getValgtTraad());
                send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
                lukkPanel(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                if (merkVMModel.getObject().isKontorsperret() && !opprettOppgavePanel.kanMerkeSomKontorsperret()) {
                    merkRadioGroup.error(getString("kontorsperre.oppgave.opprettet.feil"));
                }
                refreshFeedbackPanel(target);
            }
        };
    }

    @RunOnEvents(OPPGAVE_OPPRETTET)
    public void refreshFeedbackPanel(AjaxRequestTarget target) {
        target.add(feedbackPanel);
    }

    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
        merkVMModel.setObject(new MerkVM());
        opprettOppgavePanel.reset();
    }
}
