package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;

public interface HenvendelseBehandlingService {

    Meldinger hentMeldinger(String fnr, String valgtEnhet);

    void merkSomKontorsperret(String fnr, TraadVM valgtTraad);

    void merkSomFeilsendt(TraadVM valgtTraad);

    void merkSomBidrag(TraadVM valgtTraad);

    void merkSomAvsluttet(TraadVM valgtTraad, String enhetId);

    String getEnhet(String fnr);
}
