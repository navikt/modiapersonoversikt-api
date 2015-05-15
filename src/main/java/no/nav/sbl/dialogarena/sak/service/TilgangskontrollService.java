package no.nav.sbl.dialogarena.sak.service;


import no.nav.sbl.dialogarena.sak.tilgang.TilgangsKontrollResult;

public interface TilgangskontrollService {

    TilgangsKontrollResult harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr);
}
