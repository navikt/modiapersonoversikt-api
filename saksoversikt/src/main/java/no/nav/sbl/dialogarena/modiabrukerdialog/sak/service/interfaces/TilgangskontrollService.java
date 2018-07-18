package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TilgangskontrollService {
    TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata, String fnr, String urlTemakode);
    boolean harGodkjentEnhet(HttpServletRequest request);
    boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet);
    void markerIkkeJournalforte(List<Sakstema> sakstemaList);
}
