package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.domain.widget.Tema;

import java.util.List;

public interface SaksoversiktService {
    @SuppressWarnings("PMD")
    List<Tema> hentTemaer(String fnr);

}
