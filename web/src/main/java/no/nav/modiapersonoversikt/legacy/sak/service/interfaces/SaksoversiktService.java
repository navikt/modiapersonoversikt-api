package no.nav.modiapersonoversikt.legacy.sak.service.interfaces;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sakstema;

import java.util.List;

public interface SaksoversiktService {
    void fjernGamleDokumenter(List<Sakstema> resultat);
}
