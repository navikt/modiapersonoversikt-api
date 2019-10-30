package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon;

import java.util.Optional;

public interface OrganisasjonService {

    Optional<Organisasjon> hentNoekkelinfo(String orgnummer);

}
