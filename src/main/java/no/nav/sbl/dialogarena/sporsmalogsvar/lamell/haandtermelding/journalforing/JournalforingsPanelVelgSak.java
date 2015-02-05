package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
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
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private SakerService sakerService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

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
        return new IndicatingAjaxButtonWithImageUrl("journalforTraad", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                TraadVM valgtTraadVM = innboksVM.getValgtTraad();
                Melding melding = valgtTraadVM.getEldsteMelding().melding;
                Sak sak = valgtTraadVM.journalfortSak;

                behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                        melding.traadId,
                        sak.saksId,
                        sak.temaKode,
                        saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()
                );
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
