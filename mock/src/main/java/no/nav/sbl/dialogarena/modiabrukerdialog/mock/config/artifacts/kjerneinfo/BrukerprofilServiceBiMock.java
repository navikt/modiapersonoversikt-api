package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

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

    public static BrukerprofilServiceBi getBrukerprofilServiceBiMock() {
        BrukerprofilServiceBi serviceMock = mock(BrukerprofilServiceBi.class);
        BrukerprofilResponse mockReturnValue = createBrukerprofilResponse();

        try {
            when(serviceMock.hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class))).thenReturn(mockReturnValue);
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet | HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            throw new RuntimeException(e);
        }
        return serviceMock;
    }

    private static BrukerprofilResponse createBrukerprofilResponse() {
        BrukerprofilResponse mockReturnValue = new BrukerprofilResponse();
        mockReturnValue.setBruker(new Bruker());
        return mockReturnValue;
    }

}
