package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class JournalforingsPanel extends Panel {

    private final JournalforingsPanelEnkeltSak journalforingsPanelEnkeltSak;
    private final JournalforingsPanelVelgSak journalforingsPanelVelgSak;

    public JournalforingsPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        journalforingsPanelEnkeltSak = new JournalforingsPanelEnkeltSak("journalforingsPanelEnkeltSak", innboksVM);
        journalforingsPanelEnkeltSak.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return erValgtTraadJournalfortTidligere(innboksVM.getObject());
            }
        }));

        journalforingsPanelVelgSak = new JournalforingsPanelVelgSak("journalforingsPanelVelgSak", innboksVM);
        journalforingsPanelVelgSak.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !erValgtTraadJournalfortTidligere(innboksVM.getObject());
            }
        }));

        add(journalforingsPanelVelgSak, journalforingsPanelEnkeltSak, getAvbrytLenke());
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

    public void oppdatereJournalforingssaker() {
        journalforingsPanelVelgSak.oppdater();
        journalforingsPanelEnkeltSak.oppdater();
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        lukkJournalforingsPanel(target);
    }

    private void lukkJournalforingsPanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }


}
