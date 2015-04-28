package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class JournalforingsPanel extends Panel {
    private final JournalforingsPanelEnkeltSak journalforingsPanelEnkeltSak;
    private final JournalforingsPanelVelgSak journalforingsPanelVelgSak;
    private final IModel<Boolean> tekniskFeil = Model.of(false);

    public JournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id);

        journalforingsPanelEnkeltSak = new JournalforingsPanelEnkeltSak("journalforingsPanelEnkeltSak", innboksVM);
        journalforingsPanelVelgSak = new JournalforingsPanelVelgSak("journalforingsPanelVelgSak", innboksVM);

        AbstractReadOnlyModel<Boolean> valgtTraadErJournalfortTidligere = lagValgtTraadErJournalfortTidligereModel(innboksVM);
        journalforingsPanelEnkeltSak.add(visibleIf(both(valgtTraadErJournalfortTidligere).and(not(tekniskFeil))));
        journalforingsPanelVelgSak.add(visibleIf(both(not(valgtTraadErJournalfortTidligere)).and(not(tekniskFeil))));

        add(
                journalforingsPanelVelgSak,
                journalforingsPanelEnkeltSak,
                new WebMarkupContainer("tekniskFeilContainer").add(visibleIf(tekniskFeil)),
                new AjaxLink<InnboksVM>("avbrytJournalforing") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ((AnimertPanel) JournalforingsPanel.this.getParent()).lukkPanel(target);
                    }
                });
    }

    private AbstractReadOnlyModel<Boolean> lagValgtTraadErJournalfortTidligereModel(final InnboksVM innboksVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return (innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortDato != null);
            }
        };
    }

    public void oppdatereJournalforingssaker() {
        try {
            journalforingsPanelVelgSak.oppdater();
            journalforingsPanelEnkeltSak.oppdater();
            setTekniskFeil(false);
        } catch (Exception e) {
            setTekniskFeil(true);
        }
    }

    public void setTekniskFeil(boolean value) {
        this.tekniskFeil.setObject(value);
    }
}
