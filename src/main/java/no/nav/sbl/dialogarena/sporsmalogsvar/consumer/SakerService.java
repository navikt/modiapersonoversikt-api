package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Saker;

public interface SakerService {
    public Saker hentSaker(String fnr);
}
