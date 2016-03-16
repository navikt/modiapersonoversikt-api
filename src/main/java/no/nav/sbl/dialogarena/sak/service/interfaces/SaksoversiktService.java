package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import java.util.List;

public interface SaksoversiktService {
    @SuppressWarnings("PMD")
    List<Tema> hentTemaer(String fnr);

    void fjernGamleDokumenter(List<Sakstema> resultat);
}
