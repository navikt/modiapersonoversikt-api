package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.TjenesteResultatWrapper;

import java.util.List;

public class TilgangskontrollServiceImpl implements TilgangskontrollService {

    public TjenesteResultatWrapper harSaksbehandlerTilgangTilDokument(String journalpostId, String fnr, String sakstemakode) {
        return new TjenesteResultatWrapper(true);
    }

    @Override
    public List<Sakstema> harSaksbehandlerTilgangTilSakstema(List<Sakstema> sakstema) {
        return sakstema;
    }
}
