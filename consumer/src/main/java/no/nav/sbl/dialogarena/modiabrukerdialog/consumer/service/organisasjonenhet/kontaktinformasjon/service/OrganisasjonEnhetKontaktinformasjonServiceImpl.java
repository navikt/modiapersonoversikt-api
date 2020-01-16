package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.WSHentKontaktinformasjonForEnhetBolkResponse;

public class OrganisasjonEnhetKontaktinformasjonServiceImpl implements OrganisasjonEnhetKontaktinformasjonService {

    private final OrganisasjonEnhetKontaktinformasjonV1 service;

    public OrganisasjonEnhetKontaktinformasjonServiceImpl(OrganisasjonEnhetKontaktinformasjonV1 service) {
        this.service = service;
    }

    @Override
    public OrganisasjonEnhetKontaktinformasjon hentKontaktinformasjon(String enhetId) {
        WSHentKontaktinformasjonForEnhetBolkResponse response = hentResponse(lagRequest(enhetId));
        return response.getEnhetListe().stream()
                .map(OrganisasjonEnhetKontaktinformasjonMapper::map)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(response.getFeiletEnhetListe().stream()
                        .findFirst()
                        .map(WSFeiletEnhet::getFeilmelding)
                        .orElse("")));
    }

    private WSHentKontaktinformasjonForEnhetBolkRequest lagRequest(String enhetId) {
        WSHentKontaktinformasjonForEnhetBolkRequest request = new WSHentKontaktinformasjonForEnhetBolkRequest();
        request.getEnhetIdListe().add(enhetId);
        return request;
    }

    private WSHentKontaktinformasjonForEnhetBolkResponse hentResponse(WSHentKontaktinformasjonForEnhetBolkRequest request) {
        try {
            return service.hentKontaktinformasjonForEnhetBolk(request);
        } catch (HentKontaktinformasjonForEnhetBolkUgyldigInput e) {
            throw new RuntimeException(e);
        }
    }

}
