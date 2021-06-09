package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;

import java.util.List;

public interface SaksoversiktService {
    void fjernGamleDokumenter(List<Sakstema> resultat);
}
