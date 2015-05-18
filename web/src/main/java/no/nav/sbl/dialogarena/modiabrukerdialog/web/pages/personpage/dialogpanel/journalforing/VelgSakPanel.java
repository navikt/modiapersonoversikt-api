package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing.JournalforingsPanel.SAK_VALGT;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class VelgSakPanel extends Panel {

    @Inject
    private SakerService sakerService;

    private SakerVM sakerVM;
    private String fokusEtterLukking;

    public VelgSakPanel(String id, final String fnr, final IModel<HenvendelseVM> henvendelseVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

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
                new Label("ingenSaker", new ResourceModel("journalfor.ingensaker"))
                        .add(visibleIf(
                                both(not(sakerVM.sakerFinnes()))
                                        .and(not(sakerVM.tekniskFeil)))),
                new Label("tekniskFeil", new ResourceModel("journalfor.feilmelding.tekniskFeil")).add(visibleIf(sakerVM.tekniskFeil))
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
            ((AjaxLazyLoadVelgSakPanel) this.getParent()).lukkPanel(target);
            if (!isBlank(this.fokusEtterLukking)) {
                target.appendJavaScript(format("$('#%s').focus();", this.fokusEtterLukking));
            }
        }
    }

    public void oppdaterSaker() {
        sakerVM.oppdater();
    }

    public void settFokusEtterLukking(String markupId) {
        this.fokusEtterLukking = markupId;
    }
}
