package no.nav.nav.sbl.dialogarena.modiabrukerdialog.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;

import java.util.List;

public interface SakerService {
    public Saker hentSaker(String fnr);
    public List<Sak> hentListeAvSaker(String fnr);
}
