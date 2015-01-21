package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
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

        add(
                new Label("sakstype"),
                new Label("temaNavn"),
                new Label("saksId"),
                new Label("fagsystemNavn"),
                new Label("opprettetDatoFormatert"),
                getSubmitLenke(innboksVM)
        );
    }

    private AjaxLink getSubmitLenke(final InnboksVM innboksVM) {
        return new AjaxLink("journalforTraad") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                TraadVM valgtTraadVM = innboksVM.getValgtTraad();
                Melding melding = valgtTraadVM.getEldsteMelding().melding;
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
