package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class JournalforingsPanel extends Panel {

    public static final String TRAAD_JOURNALFORT = "sos.journalforingspanel.traadJournalfort";

    private final JournalforingsPanelEnkeltSak journalforingsPanelEnkeltSak;
    private final JournalforingsPanelVelgSak journalforingsPanelVelgSak;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalforingsPanelEnkeltSak = new JournalforingsPanelEnkeltSak("journalforingsPanelEnkeltSak", innboksVM);
        journalforingsPanelVelgSak = new JournalforingsPanelVelgSak("journalforingsPanelVelgSak", innboksVM);

        AbstractReadOnlyModel<Boolean> valgtTraadErJournalfortTidligere = lagValgtTraadErJournalfortTidligereModel(innboksVM);
        journalforingsPanelEnkeltSak.add(visibleIf(valgtTraadErJournalfortTidligere));
        journalforingsPanelVelgSak.add(visibleIf(not(valgtTraadErJournalfortTidligere)));

        add(journalforingsPanelVelgSak, journalforingsPanelEnkeltSak, getAvbrytLenke());
    }

    private AbstractReadOnlyModel<Boolean> lagValgtTraadErJournalfortTidligereModel(final IModel<InnboksVM> innboksVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return erValgtTraadJournalfortTidligere(innboksVM.getObject());
            }
        };
    }

    private boolean erValgtTraadJournalfortTidligere(InnboksVM innboksVM) {
        return(innboksVM.getValgtTraad().getEldsteMelding().melding.journalfortDato != null);
   }

    private AjaxLink<InnboksVM> getAvbrytLenke() {
        return new AjaxLink<InnboksVM>("avbrytJournalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkJournalforingsPanel(target);
            }
        };
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

    public void oppdatereJournalforingssaker() {
        journalforingsPanelVelgSak.oppdater();
        journalforingsPanelEnkeltSak.oppdater();
    }
}
