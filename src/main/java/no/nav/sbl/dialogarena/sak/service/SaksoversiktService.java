package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;

import java.util.List;
import java.util.Map;

public interface SaksoversiktService {
    @SuppressWarnings("PMD")
    List<TemaVM> hentTemaer(String fnr);

    Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(String fnr);
}
