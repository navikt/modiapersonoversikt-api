package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.dkif.consumer.DkifServiceBi;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DkifServiceBiMock {

    public static DkifServiceBi getDkifServiceBiMock() {
        DkifServiceBi serviceMock = mock(DkifServiceBi.class);
        WSHentDigitalKontaktinformasjonResponse mockReturnValue = createBrukerprofilResponse();

        try {
            when(serviceMock.hentDigitalKontaktinformasjon(any(WSHentDigitalKontaktinformasjonRequest.class))).thenReturn(mockReturnValue);
        } catch (HentDigitalKontaktinformasjonSikkerhetsbegrensing |HentDigitalKontaktinformasjonPersonIkkeFunnet e) {
            throw new RuntimeException(e);
        }

        return serviceMock;
    }

    private static WSHentDigitalKontaktinformasjonResponse createBrukerprofilResponse() {
        WSKontaktinformasjon digitalKontaktinformasjon = new WSKontaktinformasjon()
                .withPersonident("3216548765")
                .withEpostadresse(new WSEpostadresse().withValue("test@minid.com"))
                .withMobiltelefonnummer(new WSMobiltelefonnummer().withValue("13245678"))
                .withReservasjon("true");

        return new WSHentDigitalKontaktinformasjonResponse().withDigitalKontaktinformasjon(digitalKontaktinformasjon);
    }


}
