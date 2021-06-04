package no.nav.sbl.dialogarena.varsel.service;


import no.nav.sbl.dialogarena.varsel.domain.Varsel;

import java.util.List;
import java.util.Optional;

public interface VarslerService {
    Optional<List<Varsel>> hentAlleVarsler(String fnr);
}
