package no.nav.sbl.dialogarena.varsel.service;


import no.nav.sbl.dialogarena.varsel.domain.Varsel;

import java.util.List;

public interface VarslerService {
    List<Varsel> hentAlleVarsler(String fnr);
}
