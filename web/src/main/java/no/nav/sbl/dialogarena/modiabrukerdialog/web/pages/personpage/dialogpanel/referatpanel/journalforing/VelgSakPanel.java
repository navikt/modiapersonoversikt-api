package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.journalforing;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.journalforing.JournalforingsPanel.SAK_VALGT;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class VelgSakPanel extends Panel {

    @Inject
    private SakerService sakerService;

    private SakerVM sakerVM;

    public VelgSakPanel(String id, final String fnr, final IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(false);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        sakerVM = new SakerVM(sakerService, fnr);
        Form<HenvendelseVM> form = new Form<>("plukkSakForm", new CompoundPropertyModel<>(henvendelseVM));
        form.add(visibleIf(sakerVM.sakerFinnes()));
        form.add(
                feedbackPanel,
                new SakerRadioGroup("valgtSak", sakerVM),
                getSubmitLenke(feedbackPanel));
        add(
                form,
                new AjaxLink<HenvendelseVM>("avbrytJournalforing") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lukkPanel(target);
                        send(getPage(), BREADTH, new NamedEventPayload(SAK_VALGT));
                    }
                },
                new Label("ingenSaker",
                        new ResourceModel("journalfor.ingensaker")).add(visibleIf(not(sakerVM.sakerFinnes())))
        );
    }

    private AjaxButton getSubmitLenke(final FeedbackPanel feedbackPanel) {
        return new AjaxButton("velgSak") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                lukkPanel(target);
                send(getPage(), BREADTH, new NamedEventPayload(SAK_VALGT));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    private void lukkPanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            this.setVisibilityAllowed(false);
            target.prependJavaScript(format("lukket|$('#%s').slideUp(lukket)", this.getMarkupId()));
            target.add(this);
        }
    }

    public void togglePanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript(format("lukket|$('#%s').slideUp(lukket)", this.getMarkupId()));
            this.setVisibilityAllowed(false);
        } else {
            sakerVM.oppdater();
            target.appendJavaScript(format("$('#%s').slideDown()", this.getMarkupId()));
            this.setVisibilityAllowed(true);
        }
        target.add(this);
    }

}
