package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;

public class JournalforingsPanelVelgSak extends Panel {

    @Inject
    private MeldingService meldingService;

    private SakerVM sakerVM;

    public JournalforingsPanelVelgSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        sakerVM = new SakerVM(innboksVM, meldingService);
        Form<InnboksVM> form = new Form<>("plukkSakForm", new CompoundPropertyModel<>(innboksVM));
        form.add(
                feedbackPanel,
                new SakerRadioGroup("valgtTraad.journalfortSak", sakerVM),
                getSubmitLenke(innboksVM, feedbackPanel));
        add(form);
    }

    private AjaxButton getSubmitLenke(final InnboksVM innboksVM, final FeedbackPanel feedbackPanel) {
        return new AjaxButton("journalforTraad") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                TraadVM valgtTraadVM = innboksVM.getValgtTraad();
                meldingService.journalforTraad(valgtTraadVM, valgtTraadVM.journalfortSak);
                send(this, Broadcast.BUBBLE, TRAAD_JOURNALFORT);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    public void oppdater() {
        sakerVM.oppdater();
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        getParent().setVisibilityAllowed(false);
        target.add(this);
    }
}
