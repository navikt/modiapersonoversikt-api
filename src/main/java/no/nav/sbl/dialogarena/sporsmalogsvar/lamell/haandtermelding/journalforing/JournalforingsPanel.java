package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class JournalforingsPanel extends AnimertPanel {

    public static final String TRAAD_JOURNALFORT = "sos.journalforingspanel.traadJournalfort";

    private final JournalforingsPanelEnkeltSak journalforingsPanelEnkeltSak;
    private final JournalforingsPanelVelgSak journalforingsPanelVelgSak;

    public JournalforingsPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalforingsPanelEnkeltSak = new JournalforingsPanelEnkeltSak("journalforingsPanelEnkeltSak", innboksVM);
        journalforingsPanelVelgSak = new JournalforingsPanelVelgSak("journalforingsPanelVelgSak", innboksVM);

        AbstractReadOnlyModel<Boolean> valgtTraadErJournalfortTidligere = lagValgtTraadErJournalfortTidligereModel(innboksVM);
        journalforingsPanelEnkeltSak.add(visibleIf(valgtTraadErJournalfortTidligere));
        journalforingsPanelVelgSak.add(visibleIf(not(valgtTraadErJournalfortTidligere)));

        add(journalforingsPanelVelgSak, journalforingsPanelEnkeltSak, new AjaxLink<InnboksVM>("avbrytJournalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
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

    @Override
    public void togglePanel(AjaxRequestTarget target) {
        oppdatereJournalforingssaker();
        super.togglePanel(target);
    }

    @RunOnEvents({VALGT_MELDING_EVENT, TRAAD_JOURNALFORT})
    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
    }

    public void oppdatereJournalforingssaker() {
        journalforingsPanelVelgSak.oppdater();
        journalforingsPanelEnkeltSak.oppdater();
    }
}
