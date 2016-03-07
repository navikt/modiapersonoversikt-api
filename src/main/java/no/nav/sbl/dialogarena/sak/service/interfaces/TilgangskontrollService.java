package no.nav.sbl.dialogarena.sak.service.interfaces;


import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public interface TilgangskontrollService {
    boolean harSaksbehandlerTilgangTilDokument(String sakstemakode, String valgtEnhet);
    List<ModiaSakstema> harSaksbehandlerTilgangTilSakstema(List<Sakstema> sakstema, String valgtEnhet);
    Optional<Response> harGodkjentEnhet(HttpServletRequest request);
}
