package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.INNBOKS_OPPDATERT_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

@RefreshOnEvents({VALGT_MELDING_EVENT, INNBOKS_OPPDATERT_EVENT})
public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);

        add(new HaandterMeldingPanel("haandterMelding", innboksVM));
        add(new KontorsperreInfoPanel("kontorsperretInfo", innboksVM));
        add(new TraadPanel("traad", innboksVM));
    }
}
