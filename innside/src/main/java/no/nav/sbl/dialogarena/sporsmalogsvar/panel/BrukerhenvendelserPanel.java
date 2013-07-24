package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;

public class BrukerhenvendelserPanel extends Lerret {
    public BrukerhenvendelserPanel(String id, String brukerident) {
        super(id);
        setOutputMarkupId(true);
        add(new Innboks("innboks", brukerident));
    }
}