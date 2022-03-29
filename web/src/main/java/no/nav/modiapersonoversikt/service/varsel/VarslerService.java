package no.nav.modiapersonoversikt.service.varsel;


import no.nav.modiapersonoversikt.service.varsel.domain.Varsel;

import java.util.List;
import java.util.Optional;

public interface VarslerService {
    Optional<List<Varsel>> hentAlleVarsler(String fnr);
}
