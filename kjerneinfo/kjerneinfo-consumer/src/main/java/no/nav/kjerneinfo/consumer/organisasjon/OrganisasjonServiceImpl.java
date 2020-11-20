package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon;

import java.util.Optional;

import static java.util.Optional.of;

public class OrganisasjonServiceImpl implements OrganisasjonService {

    private final OrganisasjonV1RestClient organisasjonV1RestClient;

    public OrganisasjonServiceImpl(OrganisasjonV1RestClient organisasjonV1RestClient) {
        this.organisasjonV1RestClient = organisasjonV1RestClient;
    }

    public Optional<Organisasjon> hentNoekkelinfo(String orgnummer) {
        String formatterOrgNavn = formaterNavn(organisasjonV1RestClient.hentKjernInfoFraRestClient(orgnummer).getNavn().getNavnelinje1());
        return of(new Organisasjon().withNavn(formatterOrgNavn));
    }


    private String formaterNavn(String wsNavn) {
        return String.join(" ", wsNavn);
    }

}
