package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Traad;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface MeldingerSok {
    void indekser(String fnr, List<Melding> meldinger);

    List<Traad> sok(String fnr, String soketekst) throws IkkeIndeksertException;

    @Scheduled(cron = "1 * * * * *") // Hvert minutt
    void ryddOppCache();

}
