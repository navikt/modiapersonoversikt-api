package no.nav.modiapersonoversikt.integration.dkif.consumer.support;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DkifServiceImplTest {

    private static final String PERSONIDENT = "10108000398";
    private static final String EPOST = "test@testesen.no";

    private DigitalKontaktinformasjonV1 wsService;

    @Before
    public void before() {
        wsService = mock(DigitalKontaktinformasjonV1.class);
    }

    @Test
    public void henterKontaktinformasjon() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet {
        DkifServiceImpl service = new DkifServiceImpl(wsService);
        when(wsService.hentDigitalKontaktinformasjon(any(WSHentDigitalKontaktinformasjonRequest.class)))
                .thenReturn(new WSHentDigitalKontaktinformasjonResponse()
                        .withDigitalKontaktinformasjon(new WSKontaktinformasjon()
                                .withEpostadresse(new WSEpostadresse().withValue(EPOST))
                                .withPersonident(PERSONIDENT)));

        WSHentDigitalKontaktinformasjonResponse response = service.hentDigitalKontaktinformasjon(PERSONIDENT);

        assertThat(response.getDigitalKontaktinformasjon().getPersonident(), is(PERSONIDENT));
        assertThat(response.getDigitalKontaktinformasjon().getEpostadresse().getValue(), is(EPOST));
    }

    @Test
    public void lagerTomResponsNarInformasjonIkkeFunnet() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet {
        DkifServiceImpl service = new DkifServiceImpl(wsService);
        when(wsService.hentDigitalKontaktinformasjon(any(WSHentDigitalKontaktinformasjonRequest.class)))
            .thenThrow(new HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet());

        WSHentDigitalKontaktinformasjonResponse response = service.hentDigitalKontaktinformasjon(PERSONIDENT);

        assertThat(response.getDigitalKontaktinformasjon().getEpostadresse().getValue(), is(""));
        assertThat(response.getDigitalKontaktinformasjon().getMobiltelefonnummer().getValue(), is(""));
        assertThat(response.getDigitalKontaktinformasjon().getReservasjon(), is(""));
    }

    @Test(expected= ApplicationException.class)
    public void kasterExcpetionVedFeil() throws HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet, HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet {
        DkifServiceImpl service = new DkifServiceImpl(wsService);
        when(wsService.hentDigitalKontaktinformasjon(any(WSHentDigitalKontaktinformasjonRequest.class)))
                .thenThrow(new RuntimeException());

        service.hentDigitalKontaktinformasjon(PERSONIDENT);
    }
}