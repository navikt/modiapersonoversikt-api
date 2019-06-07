package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;

public interface HenvendelseBehandlingService {

    Meldinger hentMeldinger(String fnr, String valgtEnhet);

    void merkSomKontorsperret(String fnr, TraadVM valgtTraad);

    void merkSomFeilsendt(TraadVM valgtTraad);

    void merkSomBidrag(TraadVM valgtTraad);

    void merkSomAvsluttet(TraadVM valgtTraad, String enhetId);

    void merkForHastekassering(TraadVM valgtTraad);

    String getEnhet(String fnr);
}
