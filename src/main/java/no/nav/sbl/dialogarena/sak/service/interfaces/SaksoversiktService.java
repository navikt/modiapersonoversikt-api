package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import java.util.List;

public interface SaksoversiktService {
    void fjernGamleDokumenter(List<Sakstema> resultat);
}
