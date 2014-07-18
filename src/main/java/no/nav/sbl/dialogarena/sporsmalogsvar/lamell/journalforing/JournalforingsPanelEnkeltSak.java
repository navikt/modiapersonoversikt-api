package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
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

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;

public class JournalforingsPanelEnkeltSak extends Panel {

    @Inject
    private MeldingService meldingService;

    private JournalfortSakVM journalfortSakVM;

    public JournalforingsPanelEnkeltSak(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalfortSakVM = new JournalfortSakVM(innboksVM, meldingService);
        setDefaultModel(new CompoundPropertyModel<Object>(new PropertyModel<Sak>(journalfortSakVM, "sak")));

        add(new Label("sakstype"));
        add(new Label("tema"));
        add(new Label("saksId"));
        add(new Label("fagsystem"));
        add(new Label("opprettetDatoFormatert"));
        add(getSubmitLenke(innboksVM));
    }

    private AjaxLink getSubmitLenke(final InnboksVM innboksVM) {
        return new AjaxLink("journalforTraad") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                TraadVM valgtTraadVM = innboksVM.getValgtTraad();
                String fnr = innboksVM.getFnr();
                meldingService.journalforTraad(valgtTraadVM, journalfortSakVM.getSak(), fnr);
                lukkJournalforingsPanel(target);
                send(this, Broadcast.BUBBLE, TRAAD_JOURNALFORT);
            }
        };
    }

    public void oppdater() {
        journalfortSakVM.oppdater();
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        getParent().setVisibilityAllowed(false);
        target.add(getParent());
    }

}
