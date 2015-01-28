package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static org.apache.wicket.event.Broadcast.BREADTH;

@RefreshOnEvents({VALGT_MELDING_EVENT, INNBOKS_OPPDATERT_EVENT})
public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);

        add(new WebMarkupContainer("nyMelding")
                .add(new AjaxLink<InnboksVM>("besvar") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        send(getPage(), BREADTH, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
                    }
                })
                .add(visibleIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.bleInitiertAvEtSporsmal()")))
                .setOutputMarkupPlaceholderTag(true));

        add(new HaandterMeldingPanel("haandterMelding", innboksVM));
        add(new KontorsperreInfoPanel("kontorsperretInfo", innboksVM));
        add(new TraadPanel("traad", innboksVM));
    }
}
