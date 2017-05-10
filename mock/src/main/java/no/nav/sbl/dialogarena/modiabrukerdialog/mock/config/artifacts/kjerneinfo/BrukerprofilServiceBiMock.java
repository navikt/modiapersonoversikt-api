package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.brukerprofil.consumer.messages.BrukerprofilResponse;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.brukerprofil.domain.TilrettelagtKommunikasjon;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrukerprofilServiceBiMock {

    public static BrukerprofilServiceBi getBrukerprofilServiceBiMock() {
        BrukerprofilServiceBi serviceMock = mock(BrukerprofilServiceBi.class);
        BrukerprofilResponse mockReturnValue = createBrukerprofilResponse();

        try {
            when(serviceMock.hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class))).thenReturn(mockReturnValue);
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet |
                HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning |
                HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            throw new RuntimeException(e);
        }
        return serviceMock;
    }

    private static BrukerprofilResponse createBrukerprofilResponse() {
        BrukerprofilResponse mockReturnValue = new BrukerprofilResponse();
        Bruker bruker = new Bruker();
        bruker.setTilrettelagtKommunikasjon(asList(
                new TilrettelagtKommunikasjon().withBehov("LESA").withBeskrivelse("Ledsager"),
                new TilrettelagtKommunikasjon().withBehov("TOHJ").withBeskrivelse("Tolkehjelp")
        ));
        mockReturnValue.setBruker(bruker);
        return mockReturnValue;
    }

}
