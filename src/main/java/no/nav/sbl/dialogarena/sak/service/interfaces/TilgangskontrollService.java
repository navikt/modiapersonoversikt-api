package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.servlet.http.HttpServletRequest;

public interface TilgangskontrollService {
    TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata);
    boolean harGodkjentEnhet(HttpServletRequest request);
    boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet);
}
