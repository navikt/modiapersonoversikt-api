package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.JoarkJournalforingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
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
    private JoarkJournalforingService joarkJournalforingService;

    private JournalfortSakVM journalfortSakVM;

    public JournalforingsPanelEnkeltSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalfortSakVM = new JournalfortSakVM(innboksVM);
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
                joarkJournalforingService.journalforTraad(valgtTraadVM, journalfortSakVM.getSak());
                send(getPage(), Broadcast.DEPTH, TRAAD_JOURNALFORT);
            }
        };
    }

    public void oppdater() {
        journalfortSakVM.oppdater();
    }

}
