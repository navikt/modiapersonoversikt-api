package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;

import java.util.List;

public interface HenvendelseBehandlingService {
    List<Melding> hentMeldinger(String fnr);

    List<Melding> hentMeldinger(String fnr, String valgtEnhet);

    void merkSomKontorsperret(String fnr, TraadVM valgtTraad);

    void merkSomFeilsendt(TraadVM valgtTraad);

    void merkSomBidrag(TraadVM valgtTraad);
}
