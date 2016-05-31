package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.TjenesteResultatWrapper;

public interface TilgangskontrollService {
    TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr, String sakstemakode);
}
