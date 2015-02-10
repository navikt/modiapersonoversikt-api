package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;

public class JournalforingsPanelEnkeltSak extends Panel {

    @Inject
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private SakerService sakerService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private JournalfortSakVM journalfortSakVM;

    public JournalforingsPanelEnkeltSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalfortSakVM = new JournalfortSakVM(innboksVM, sakerService);
        setDefaultModel(new CompoundPropertyModel<Object>(new PropertyModel<Sak>(journalfortSakVM, "sak")));

        Form form = new Form("journalforForm");
        form.add(
                new Label("sakstype"),
                new Label("temaNavn"),
                new Label("saksId"),
                new Label("fagsystemNavn"),
                new Label("opprettetDatoFormatert"),
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

                behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                        melding.traadId,
                        sak.saksId,
                        sak.temaKode,
                        saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
                send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
            }
        };
    }

    public void oppdater() {
        journalfortSakVM.oppdater();
    }

}
