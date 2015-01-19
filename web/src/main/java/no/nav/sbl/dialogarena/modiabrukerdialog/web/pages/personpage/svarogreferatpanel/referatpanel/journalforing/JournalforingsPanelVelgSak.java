package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.journalforing;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.journalforing.JournalforingsPanel.JOURNALFORING_VELG_SAK_PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class JournalforingsPanelVelgSak extends AnimertPanel {

    @Inject
    private SakerService sakerService;

    private SakerVM sakerVM;

    public JournalforingsPanelVelgSak(String id, final String fnr, IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        sakerVM = new SakerVM(sakerService, fnr);
        Form<HenvendelseVM> form = new Form<>("plukkSakForm", new CompoundPropertyModel<>(henvendelseVM));
        form.add(
                feedbackPanel,
                new SakerRadioGroup("valgtSak", sakerVM),
                getSubmitLenke(feedbackPanel));
        form.add(visibleIf(sakerVM.sakerFinnes()));
        add(form);
        add(
                new Label("ingenSaker",
                        new ResourceModel("journalfor.ingensaker")).add(visibleIf(not(sakerVM.sakerFinnes()))),
                new AjaxLink<InnboksVM>("avbrytJournalforing") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lukkPanel(target);
                        send(getPage(), BREADTH, new NamedEventPayload(JOURNALFORING_VELG_SAK_PANEL_TOGGLET));
                    }
                }
        );
    }

    private AjaxButton getSubmitLenke(final FeedbackPanel feedbackPanel) {
        return new AjaxButton("velgSak") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                lukkPanel(target);
                send(getPage(), BREADTH, new NamedEventPayload(JOURNALFORING_VELG_SAK_PANEL_TOGGLET));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    @Override
    public void togglePanel(AjaxRequestTarget target) {
        sakerVM.oppdater();
        super.togglePanel(target);
    }

}
