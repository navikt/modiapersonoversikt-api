package no.nav.modiapersonoversikt.legacy.api.service.psak;

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;

import java.util.List;

public interface PsakService {

    List<Sak> hentSakerFor(String fnr);
}
