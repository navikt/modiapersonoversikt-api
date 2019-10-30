package no.nav.dkif.config.spring;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.*;

public class DkifMock implements DigitalKontaktinformasjonV1 {

    @Override
    public WSHentSikkerDigitalPostadresseBolkResponse hentSikkerDigitalPostadresseBolk(WSHentSikkerDigitalPostadresseBolkRequest wsHentSikkerDigitalPostadresseBolkRequest) throws HentSikkerDigitalPostadresseBolkForMangeForespoersler, HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing {
        return null;
    }

    @Override
    public WSHentPrintsertifikatResponse hentPrintsertifikat(WSHentPrintsertifikatRequest wsHentPrintsertifikatRequest) {
        return null;
    }

    @Override
    public void ping() {

    }

    @Override
    public WSHentSikkerDigitalPostadresseResponse hentSikkerDigitalPostadresse(WSHentSikkerDigitalPostadresseRequest wsHentSikkerDigitalPostadresseRequest) throws HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet, HentSikkerDigitalPostadresseSikkerhetsbegrensing, HentSikkerDigitalPostadressePersonIkkeFunnet {
        return null;
    }

    @Override
    public WSHentDigitalKontaktinformasjonBolkResponse hentDigitalKontaktinformasjonBolk(WSHentDigitalKontaktinformasjonBolkRequest wsHentDigitalKontaktinformasjonBolkRequest) throws HentDigitalKontaktinformasjonBolkForMangeForespoersler, HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing {
        return null;
    }

    @Override
    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest wsHentDigitalKontaktinformasjonRequest) {
        WSKontaktinformasjon digitalKontaktinformasjon = new WSKontaktinformasjon();
        digitalKontaktinformasjon.setMobiltelefonnummer(new WSMobiltelefonnummer().withValue("12345678"));
        digitalKontaktinformasjon.setEpostadresse(new WSEpostadresse().withValue("test@testesen.com"));
        digitalKontaktinformasjon.setReservasjon("true");

        return new WSHentDigitalKontaktinformasjonResponse().withDigitalKontaktinformasjon(digitalKontaktinformasjon);
    }

}
