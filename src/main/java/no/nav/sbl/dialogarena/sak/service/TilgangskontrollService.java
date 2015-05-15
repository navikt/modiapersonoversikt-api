package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.VedleggResultat;

public interface TilgangskontrollService {
    VedleggResultat harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr);
}
