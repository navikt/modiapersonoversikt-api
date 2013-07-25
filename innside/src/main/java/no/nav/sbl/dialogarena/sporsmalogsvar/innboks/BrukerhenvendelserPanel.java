package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.modia.lamell.Lerret;

public class BrukerhenvendelserPanel extends Lerret {
    public BrukerhenvendelserPanel(String id, String brukerident) {
        super(id);
        setOutputMarkupId(true);
        add(new Innboks("innboks", brukerident));
    }
}