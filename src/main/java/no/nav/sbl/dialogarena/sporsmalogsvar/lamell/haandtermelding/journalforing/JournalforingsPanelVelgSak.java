package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.JoarkJournalforingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;

public class JournalforingsPanelVelgSak extends Panel {

    @Inject
    private JoarkJournalforingService joarkJournalforingService;
    @Inject
    private SakerService sakerService;

    private SakerVM sakerVM;

    public JournalforingsPanelVelgSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        sakerVM = new SakerVM(innboksVM, sakerService);
        Form<InnboksVM> form = new Form<>("plukkSakForm", new CompoundPropertyModel<>(innboksVM));
        form.add(
                feedbackPanel,
                new SakerRadioGroup("valgtTraad.journalfortSak", sakerVM),
                getSubmitLenke(innboksVM, feedbackPanel));
        form.add(visibleIf(sakerVM.sakerFinnes()));
        add(form);
        add(new Label("ingenSaker", new ResourceModel("journalfor.ingensaker")).add(visibleIf(not(sakerVM.sakerFinnes()))));
    }

    private AjaxButton getSubmitLenke(final InnboksVM innboksVM, final FeedbackPanel feedbackPanel) {
        return new AjaxButton("journalforTraad") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                TraadVM valgtTraadVM = innboksVM.getValgtTraad();
                joarkJournalforingService.journalforTraad(valgtTraadVM, valgtTraadVM.journalfortSak);
                send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
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

}
