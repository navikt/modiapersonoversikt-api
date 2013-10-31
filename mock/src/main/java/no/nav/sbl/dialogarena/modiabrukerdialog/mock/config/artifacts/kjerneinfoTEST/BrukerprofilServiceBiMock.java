package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfoTEST;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrukerprofilServiceBiMock {

    public static BrukerprofilServiceBi getBrukerprofilServiceBiMock() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        BrukerprofilServiceBi serviceMock = mock(BrukerprofilServiceBi.class);
        BrukerprofilResponse mockReturnValue = createBrukerprofilResponse();

        when(serviceMock.hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class))).thenReturn(mockReturnValue);
        return serviceMock;
    }

    private static BrukerprofilResponse createBrukerprofilResponse() {
        BrukerprofilResponse mockReturnValue = new BrukerprofilResponse();
        mockReturnValue.setBruker(new Bruker());
        return mockReturnValue;
    }

}
