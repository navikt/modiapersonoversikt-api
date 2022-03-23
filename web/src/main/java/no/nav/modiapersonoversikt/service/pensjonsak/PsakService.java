package no.nav.modiapersonoversikt.service.pensjonsak;

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;

import java.util.List;

public interface PsakService {

    List<Sak> hentSakerFor(String fnr);
}
