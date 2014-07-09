package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

public class JournalforingsPanelEnkeltSak extends Panel {

    private JournalfortSakVM journalfortSakVM;

    @Inject
    private MeldingService meldingService;

    public JournalforingsPanelEnkeltSak(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalfortSakVM = new JournalfortSakVM(innboksVM.getObject(), meldingService);

        WebMarkupContainer enkeltSak = new WebMarkupContainer("enkeltSak", new CompoundPropertyModel<>(journalfortSakVM));
        enkeltSak.add(new Label("sak.tema"));
        enkeltSak.add(new Label("sak.saksId"));
        enkeltSak.add(new Label("sak.fagsystem"));
        enkeltSak.add(new Label("sak.journalfortDatoFormatert", new Model<String>() {
            @Override
            public String getObject() {
                return Datoformat.kortMedTid(journalfortSakVM.getSak().opprettetDato);
            }
        }));
        enkeltSak.add(new Label("sak.sakstype"));

        add(enkeltSak, getSubmitLenke(innboksVM), getAvbrytLenke());
    }

    private AjaxLink getSubmitLenke(final IModel<InnboksVM> innboksVM) {
        return new AjaxLink("journalforTraad") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                TraadVM valgtTraadVM = innboksVM.getObject().getValgtTraad();
                meldingService.journalforTraad(valgtTraadVM, journalfortSakVM.getSak());
                lukkJournalforingsPanel(target);
            }

        };
    }

    private AjaxLink<InnboksVM> getAvbrytLenke() {
        return new AjaxLink<InnboksVM>("avbrytJournalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkJournalforingsPanel(target);
            }
        };
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

}
