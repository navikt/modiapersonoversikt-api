package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface MeldingerSok {
    void indekser(String fnr, List<Melding> meldinger);

    List<Traad> sok(String fnr, String soketekst) throws IkkeIndeksertException;

    @Scheduled(cron = "1 * * * * *") // Hvert minutt
    void ryddOppCache();

}
