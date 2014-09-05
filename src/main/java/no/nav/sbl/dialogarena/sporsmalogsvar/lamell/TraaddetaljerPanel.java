package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

@RefreshOnEvents({VALGT_MELDING_EVENT, MELDING_SENDT_TIL_BRUKER, TRAAD_MERKET, TRAAD_JOURNALFORT})
public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);

        add(new HaandterMeldingPanel("haandterMelding", innboksVM));
        add(new NyesteMeldingPanel("nyesteMelding", innboksVM));
        add(new TidligereMeldingerPanel("tidligereMeldinger", innboksVM));
    }
}
