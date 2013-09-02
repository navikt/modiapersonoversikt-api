package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;

public class BrukerhenvendelserPanel extends Lerret {
    public BrukerhenvendelserPanel(String id, String brukerident, MeldingService meldingService) {
        super(id);
        setOutputMarkupId(true);
        add(new Innboks("innboks", brukerident, meldingService));
    }
}