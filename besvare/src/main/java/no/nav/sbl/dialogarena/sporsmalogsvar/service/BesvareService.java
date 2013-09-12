package no.nav.sbl.dialogarena.sporsmalogsvar.service;

public interface BesvareService {

    void besvareSporsmal(Svar svar);
    BesvareSporsmalDetaljer hentDetaljer(String fnr, String oppgaveId);
}
