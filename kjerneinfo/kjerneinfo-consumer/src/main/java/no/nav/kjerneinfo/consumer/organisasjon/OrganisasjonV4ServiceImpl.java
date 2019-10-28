package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentNoekkelinfoOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSSammensattNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentNoekkelinfoOrganisasjonResponse;

import java.util.Optional;

import static java.util.Optional.of;

public class OrganisasjonV4ServiceImpl implements OrganisasjonService {

    private final OrganisasjonV4 organisasjonV4;

    public OrganisasjonV4ServiceImpl(OrganisasjonV4 organisasjonV4) {
        this.organisasjonV4 = organisasjonV4;
    }

    public Optional<Organisasjon> hentNoekkelinfo(String orgnummer) {
        WSHentNoekkelinfoOrganisasjonRequest request = new WSHentNoekkelinfoOrganisasjonRequest()
                .withOrgnummer(orgnummer);

        try {
            return of(mapNoekkelinfo(organisasjonV4.hentNoekkelinfoOrganisasjon(request)));
        } catch (HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet hentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet) {
            return Optional.empty();
        } catch (HentNoekkelinfoOrganisasjonUgyldigInput hentNoekkelinfoOrganisasjonUgyldigInput) {
            throw new ApplicationException("Ugyldig organisasjonsnummer (" + orgnummer + ") ved kall på hentNoekkelinfo",
                    hentNoekkelinfoOrganisasjonUgyldigInput);
        }
    }

    private Organisasjon mapNoekkelinfo(WSHentNoekkelinfoOrganisasjonResponse response) {
        return new Organisasjon().withNavn(formaterNavn(response.getNavn()));
    }

    private String formaterNavn(WSSammensattNavn wsNavn) {
        WSUstrukturertNavn navn = (WSUstrukturertNavn) wsNavn;
        return String.join(" ", navn.getNavnelinje());
    }

}
