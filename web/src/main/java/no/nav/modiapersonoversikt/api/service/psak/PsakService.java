package no.nav.modiapersonoversikt.api.service.psak;

import no.nav.modiapersonoversikt.api.domain.saker.Sak;

import java.util.List;

public interface PsakService {

    List<Sak> hentSakerFor(String fnr);
}
