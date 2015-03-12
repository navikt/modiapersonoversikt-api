package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;

public class JournalforingsPanelEnkeltSak extends Panel {

    @Inject
    private SakerService sakerService;

    private JournalfortSakVM journalfortSakVM;
    private FeedbackPanel feedbackPanel;

    public JournalforingsPanelEnkeltSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        journalfortSakVM = new JournalfortSakVM(innboksVM, sakerService);
        setDefaultModel(new CompoundPropertyModel<Object>(new PropertyModel<Sak>(journalfortSakVM, "sak")));

        Form form = new Form("journalforForm");
        form.add(
                new Label("sakstype"),
                new Label("temaNavn"),
                new Label("saksIdVisning"),
                new Label("fagsystemNavn"),
                new Label("opprettetDatoFormatert"),
                feedbackPanel,
                getSubmitLenke(innboksVM)
        );

        add(form);
    }

    private AjaxButton getSubmitLenke(final InnboksVM innboksVM) {
        return new IndicatingAjaxButtonWithImageUrl("journalforTraad", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Melding melding = innboksVM.getValgtTraad().getEldsteMelding().melding;
                Sak sak = journalfortSakVM.getSak();

                try {
                    sakerService.knyttBehandlingskjedeTilSak(innboksVM.getFnr(), melding.traadId, sak);
                } catch (JournalforingFeilet e) {
                    error(getString("journalfor.feilmelding.baksystem"));
                    onError(target, form);
                }
                send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    public void oppdater() {
        journalfortSakVM.oppdater();
    }

}
