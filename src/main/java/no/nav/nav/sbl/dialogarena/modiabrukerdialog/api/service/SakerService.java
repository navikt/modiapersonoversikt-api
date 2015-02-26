package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;

import java.util.List;

public interface SakerService {
    public Saker hentSaker(String fnr);

    public List<Sak> hentListeAvSaker(String fnr);

    void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak) throws Exception;
}
