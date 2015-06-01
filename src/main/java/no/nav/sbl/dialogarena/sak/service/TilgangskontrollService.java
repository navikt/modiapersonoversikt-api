package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.HentDokumentResultat;

public interface TilgangskontrollService {
    HentDokumentResultat harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr);
}
