package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import com.codahale.metrics.Timer;
import no.nav.modig.modia.metrics.MetricsFactory;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
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
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPGAVE_OPPRETTET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPRETT_OPPGAVE_TOGGLET;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_MERKET = "sos.merkepanel.merket";

    @Inject
    private HenvendelseBehandlingService henvendelseService;

    private final InnboksVM innboksVM;
    private final KontorsperrePanel kontorsperrePanel;
    private final FeedbackPanel feedbackPanel;
    private final CompoundPropertyModel<MerkVM> merkVM;
    private final AjaxButton merkKnapp;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id);

        this.innboksVM = innboksVM;

        merkVM = new CompoundPropertyModel<>(new MerkVM());
        Form<MerkVM> merkForm = new Form<>("merkForm", merkVM);

        final RadioGroup<MerkType> merkRadioGroup = new RadioGroup<>("merkType");

        feedbackPanel = new FeedbackPanel("feedbackMerkPanel", new ComponentFeedbackMessageFilter(merkRadioGroup));
        feedbackPanel.setOutputMarkupId(true);
        merkForm.add(feedbackPanel);

        PropertyModel<Boolean> valgtTraadErKontorsperret = new PropertyModel<>(innboksVM, "valgtTraad.erKontorsperret()");

        merkRadioGroup.setRequired(true);
        merkRadioGroup.add(new Radio<>("feilsendtRadio", Model.of(FEILSENDT)));
        merkRadioGroup.add(new WebMarkupContainer("bidragRadioValg")
                .add(new Radio<>("bidragRadio", Model.of(BIDRAG)))
                .add(visibleIf(not(valgtTraadErKontorsperret))));
        merkRadioGroup.add(new WebMarkupContainer("kontorsperretRadioValg")
                .add(new Radio<>("kontorsperretRadio", Model.of(KONTORSPERRET)))
                .add(visibleIf(not(valgtTraadErKontorsperret))));

        kontorsperrePanel = new KontorsperrePanel("kontorsperrePanel", innboksVM);
        kontorsperrePanel.add(visibleIf(new PropertyModel<Boolean>(merkVM, "erKontorsperret()")));

        merkKnapp = new MerkKnapp("merk");

        merkRadioGroup.add(kontorsperrePanel);
        merkRadioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kontorsperrePanel, merkKnapp);
                refreshFeedbackPanel(target);
            }
        });

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
        merkVM.setObject(new MerkVM());
        kontorsperrePanel.reset();
    }

    private class MerkKnapp extends IndicatingAjaxButtonWithImageUrl {

        public MerkKnapp(String id) {
            super(id, "../img/ajaxloader/svart/loader_svart_48.gif");
            add(visibleIf(
                    either(new PropertyModel<Boolean>(kontorsperrePanel, "kanMerkeSomKontorsperret()"))
                            .or(new PropertyModel<Boolean>(merkVM, "erFeilsendt()"))
                            .or(new PropertyModel<Boolean>(merkVM, "erMerketBidrag()"))));
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            Timer.Context timer = MetricsFactory.createTimer("hendelse.merk." + merkVM.getObject().getMerkType() + ".time").time();
            try {
                switch (merkVM.getObject().getMerkType()) {
                    case FEILSENDT:
                        haandterFeilsendt(target);
                        break;
                    case BIDRAG:
                        haandterBidrag(target);
                        break;
                    case KONTORSPERRET:
                        haandterKontorsperring(target, form);
                        break;
                }
            } finally {
                timer.stop();
            }
        }

        private void haandterFeilsendt(AjaxRequestTarget target) {
            henvendelseService.merkSomFeilsendt(innboksVM.getValgtTraad());
            send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
            lukkPanel(target);
        }

        private void haandterBidrag(AjaxRequestTarget target) {
            henvendelseService.merkSomBidrag(innboksVM.getValgtTraad());
            send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
            lukkPanel(target);
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

        @Override
        protected final void onError(AjaxRequestTarget target, Form<?> form) {
            refreshFeedbackPanel(target);
        }
    }
}
