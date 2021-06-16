package no.nav.modiapersonoversikt.legacy.sak.service.interfaces;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sakstema;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface TilgangskontrollService {
    TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(HttpServletRequest request, DokumentMetadata journalpostMetadata, String fnr, String urlTemakode);
    boolean harEnhetTilgangTilTema(String temakode, String valgtEnhet);
    void markerIkkeJournalforte(List<Sakstema> sakstemaList);
}
