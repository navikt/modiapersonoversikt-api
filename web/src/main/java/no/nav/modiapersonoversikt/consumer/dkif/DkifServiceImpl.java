package no.nav.modiapersonoversikt.consumer.dkif;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DkifServiceImpl implements Dkif.Service {

    private static final Logger logger = LoggerFactory.getLogger(DkifServiceImpl.class);
    private final DigitalKontaktinformasjonV1 dkifPorttype;

    public DkifServiceImpl(DigitalKontaktinformasjonV1 dkifPorttype) {
        this.dkifPorttype = dkifPorttype;
    }

    @Override
    public Dkif.DigitalKontaktinformasjon hentDigitalKontaktinformasjon(String ident) {
        try {
            WSHentDigitalKontaktinformasjonResponse response = dkifPorttype
                    .hentDigitalKontaktinformasjon(new WSHentDigitalKontaktinformasjonRequest().withPersonident(ident));
            return DkifSoapExtentions.responseFromDTO(response);
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            logger.info("Kunne ikke hente kontaktinformasjon fra dkif, ", e);
            return DkifSoapExtentions.responseFromDTO(lagTomDigitalKontaktinformasjonResponse());
        } catch (Exception e) {
            logger.error("Feil ved henting fra dkif, ", e.getMessage());
            throw new RuntimeException("Feil ved henting fra dkif ", e);
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
