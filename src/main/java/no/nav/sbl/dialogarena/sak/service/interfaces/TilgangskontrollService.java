package no.nav.sbl.dialogarena.sak.service.interfaces;


import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import java.util.List;

public interface TilgangskontrollService {
    boolean harSaksbehandlerTilgangTilDokument(String sakstemakode, String valgtEnhet);
    List<ModiaSakstema> harSaksbehandlerTilgangTilSakstema(List<Sakstema> sakstema, String valgtEnhet);
}
