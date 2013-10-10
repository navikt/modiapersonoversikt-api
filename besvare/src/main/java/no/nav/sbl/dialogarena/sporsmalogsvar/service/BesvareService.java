package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.modig.lang.option.Optional;

public interface BesvareService {

    void besvareSporsmal(Svar svar);
    Optional<BesvareSporsmalDetaljer> hentDetaljer(String fnr, String oppgaveId);
}
