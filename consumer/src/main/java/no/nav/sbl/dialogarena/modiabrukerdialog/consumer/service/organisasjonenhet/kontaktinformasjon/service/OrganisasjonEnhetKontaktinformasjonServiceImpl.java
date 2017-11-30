package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.FeiletEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;

public class OrganisasjonEnhetKontaktinformasjonServiceImpl implements OrganisasjonEnhetKontaktinformasjonService {

    private final OrganisasjonEnhetKontaktinformasjonV1 service;

    public OrganisasjonEnhetKontaktinformasjonServiceImpl(OrganisasjonEnhetKontaktinformasjonV1 service) {
        this.service = service;
    }

    @Override
    public OrganisasjonEnhetKontaktinformasjon hentKontaktinformasjon(String enhetId) {
        HentKontaktinformasjonForEnhetBolkResponse response = hentResponse(lagRequest(enhetId));
        return response.getEnhetListe().stream()
                .map(OrganisasjonEnhetKontaktinformasjonMapper::map)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(response.getFeiletEnhetListe().stream()
                        .findFirst()
                        .map(FeiletEnhet::getFeilmelding)
                        .orElse("")));
    }

    private HentKontaktinformasjonForEnhetBolkRequest lagRequest(String enhetId) {
        HentKontaktinformasjonForEnhetBolkRequest request = new HentKontaktinformasjonForEnhetBolkRequest();
        request.getEnhetIdListe().add(enhetId);
        return request;
    }

    private HentKontaktinformasjonForEnhetBolkResponse hentResponse(HentKontaktinformasjonForEnhetBolkRequest request) {
        try {
            return service.hentKontaktinformasjonForEnhetBolk(request);
        } catch (HentKontaktinformasjonForEnhetBolkUgyldigInput e) {
            throw new RuntimeException(e);
        }
    }

}
