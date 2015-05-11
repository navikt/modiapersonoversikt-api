package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;

import java.util.List;

public interface SakerService {
    public Saker hentSaker(String fnr);

    public List<Sak> hentListeAvSaker(String fnr);

    void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak) throws JournalforingFeilet;
}
