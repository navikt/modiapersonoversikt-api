package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SakerService {

    List<Sak> hentSammensatteSaker(String fnr);

    List<Sak> hentPensjonSaker(String fnr);

    void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak, String enhet) throws JournalforingFeilet;
}
