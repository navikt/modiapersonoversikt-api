package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel;
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
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.FEILSENDT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.KONTORSPERRET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPGAVE_OPPRETTET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPRETT_OPPGAVE_TOGGLET;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_MERKET = "sos.merkepanel.merket";

    @Inject
    private HenvendelseBehandlingService henvendelseService;

    private final InnboksVM innboksVM;
    private final KontorsperrePanel kontorsperrePanel;
    private final FeedbackPanel feedbackPanel;
    private final CompoundPropertyModel<MerkVM> merkVMModel;
    private final AjaxButton merkKnapp;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        this.innboksVM = innboksVM;

        merkVMModel = new CompoundPropertyModel<>(new MerkVM());
        final Form<MerkVM> merkForm = new Form<>("merkForm", merkVMModel);

        final RadioGroup<MerkType> merkRadioGroup = new RadioGroup<>("merkType");

        feedbackPanel = new FeedbackPanel("feedbackMerkPanel", new ComponentFeedbackMessageFilter(merkRadioGroup));
        feedbackPanel.setOutputMarkupId(true);
        merkForm.add(feedbackPanel);

        merkRadioGroup.setRequired(true);
        merkRadioGroup.add(new Radio<>("feilsendtRadio", Model.of(FEILSENDT)));
        merkRadioGroup.add(new Radio<>("kontorsperretRadio", Model.of(KONTORSPERRET)));

        kontorsperrePanel = new KontorsperrePanel("kontorsperrePanel", innboksVM);
        kontorsperrePanel.add(visibleIf(new PropertyModel<Boolean>(merkVMModel, "erKontorsperret()")));

        merkRadioGroup.add(kontorsperrePanel);
        merkRadioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kontorsperrePanel);
                refreshFeedbackPanel(target);
            }
        });

        merkKnapp = new MerkKnapp("merk");

        merkForm.add(merkRadioGroup, merkKnapp);

        add(merkForm);
        add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });
    }

    @RunOnEvents({OPPRETT_OPPGAVE_TOGGLET, OPPGAVE_OPPRETTET})
    public final void refreshMerkKnapp(AjaxRequestTarget target) {
        target.add(merkKnapp);
    }

    @RunOnEvents(OPPGAVE_OPPRETTET)
    public final void refreshFeedbackPanel(AjaxRequestTarget target) {
        target.add(feedbackPanel);
    }

    @RunOnEvents(OPPGAVE_OPPRETTET)
    public final void focusMerkKnapp(AjaxRequestTarget target) {
        target.appendJavaScript("$('#" + merkKnapp.getMarkupId() + "').focus();");
    }

    @Override
    public final void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
        merkVMModel.setObject(new MerkVM());
        kontorsperrePanel.reset();
    }

    private class MerkKnapp extends AjaxButton {

        public MerkKnapp(String id) {
            super(id);
            add(visibleIf(either(not(kontorsperrePanel.skalOppretteOppgave)).or(kontorsperrePanel.oppgaveErOpprettet)));
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            if (merkVMModel.getObject().getMerkType() == KONTORSPERRET) {
                haandterKontorsperring(target, form);
            } else {
                haandterFeilsendt(target);
            }
        }

        private void haandterKontorsperring(AjaxRequestTarget target, Form<?> form) {
            if (kontorsperrePanel.kanMerkeSomKontorsperret()) {
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
        protected final void onError(AjaxRequestTarget target, Form<?> form) {
            refreshFeedbackPanel(target);
        }
    }
}
