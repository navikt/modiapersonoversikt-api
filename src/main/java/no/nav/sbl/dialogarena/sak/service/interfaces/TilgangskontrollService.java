package no.nav.sbl.dialogarena.sak.service.interfaces;


import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;

import java.util.List;

public interface TilgangskontrollService {
    TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr, String sakstemakode);
    List<ModiaSakstema> harSaksbehandlerTilgangTilSakstema(List<Sakstema> sakstema, String valgtEnhet);
}
