package no.nav.sbl.dialogarena.varsel.service;


import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;

import java.util.List;

public interface VarslerService {
    Optional<List<Varsel>> hentAlleVarsler(String fnr);
}
