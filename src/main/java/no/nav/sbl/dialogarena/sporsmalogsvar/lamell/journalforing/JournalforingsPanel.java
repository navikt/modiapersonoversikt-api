package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class JournalforingsPanel extends Panel {

    @Inject
    private MeldingService meldingService;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final FeedbackPanel feedbackPanel = getFeedbackPanel();
        SakerVM sakerVM = new SakerVM(innboksVM.getObject(), meldingService);
        Form<InnboksVM> form = new Form<>("plukkSakForm", innboksVM);
        form.add(
                feedbackPanel,
                new SakerRadioGroup("valgtTraad.journalfortSak", sakerVM),
                getSubmitLenke(innboksVM, feedbackPanel),
                getAvbrytLenke());
        add(form);
    }

    private FeedbackPanel getFeedbackPanel() {
        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        return feedbackPanel;
    }

    private AjaxSubmitLink getSubmitLenke(final IModel<InnboksVM> innboksVM, final FeedbackPanel feedbackPanel) {
        return new AjaxSubmitLink("journalforTraad") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                TraadVM valgtTraadVM = innboksVM.getObject().getValgtTraad();
                meldingService.journalforTraad(valgtTraadVM,valgtTraadVM.journalfortSak);
                lukkJournalforingsPanel(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    private AjaxLink<InnboksVM> getAvbrytLenke() {
        return new AjaxLink<InnboksVM>("avbrytJournalforing")
            {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    lukkJournalforingsPanel(target);
                }
            };
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        lukkJournalforingsPanel(target);
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

}
