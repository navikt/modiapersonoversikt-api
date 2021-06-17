package no.nav.modiapersonoversikt.legacy.varsel.service;


import no.nav.modiapersonoversikt.legacy.varsel.domain.Varsel;

import java.util.List;
import java.util.Optional;

public interface VarslerService {
    Optional<List<Varsel>> hentAlleVarsler(String fnr);
}
