package no.nav.modiapersonoversikt.integration.dkif.consumer.support;

import no.nav.modiapersonoversikt.integration.dkif.consumer.DkifService;
import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DkifServiceImpl implements DkifService {

    private static final Logger logger = LoggerFactory.getLogger(DkifServiceImpl.class);
    private DigitalKontaktinformasjonV1 dkifService;

    public DkifServiceImpl(DigitalKontaktinformasjonV1 dkifService) {
        this.dkifService = dkifService;
    }

    @Override
    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(String ident) {
        try {
            return dkifService.hentDigitalKontaktinformasjon(new WSHentDigitalKontaktinformasjonRequest().withPersonident(ident));
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            logger.info("Kunne ikke hente kontaktinformasjon fra dkif, ", e);
            return lagTomDigitalKontaktinformasjonResponse();
        } catch (Exception e) {
            logger.error("Feil ved henting fra dkif, ", e.getMessage());
            throw new ApplicationException("Feil ved henting fra dkif ", e);
        }
    }

    private WSHentDigitalKontaktinformasjonResponse lagTomDigitalKontaktinformasjonResponse() {
        WSKontaktinformasjon digitalKontaktinformasjon = new WSKontaktinformasjon();
        digitalKontaktinformasjon.setMobiltelefonnummer(new WSMobiltelefonnummer().withValue(""));
        digitalKontaktinformasjon.setEpostadresse(new WSEpostadresse().withValue(""));
        digitalKontaktinformasjon.setReservasjon("");

        return new WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(digitalKontaktinformasjon);
    }

}
