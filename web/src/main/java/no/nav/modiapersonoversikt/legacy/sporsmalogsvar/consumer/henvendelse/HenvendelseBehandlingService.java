package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse;

import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy.TraadVM;

public interface HenvendelseBehandlingService {

    Meldinger hentMeldinger(String fnr, String valgtEnhet);

    void merkSomKontorsperret(String fnr, TraadVM valgtTraad);

    void merkSomFeilsendt(TraadVM valgtTraad);

    void merkSomBidrag(TraadVM valgtTraad);

    void merkSomAvsluttet(TraadVM valgtTraad, String enhetId);

    void merkForHastekassering(TraadVM valgtTraad);

    String getEnhet(String fnr);
}
