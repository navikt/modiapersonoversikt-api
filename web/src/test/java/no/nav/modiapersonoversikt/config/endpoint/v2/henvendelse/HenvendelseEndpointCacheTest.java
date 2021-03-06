package no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse;

import no.nav.modiapersonoversikt.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.common.utils.SslUtils.setupTruststore;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class HenvendelseEndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "endpointCache";

    @Autowired
    private HenvendelsePortType henvendelse;

    public HenvendelseEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    public static void setup() {
        setupTruststore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHentHenvendelse() {
        WSHentHenvendelseRequest req1 = new WSHentHenvendelseRequest().withBehandlingsId("a");
        WSHentHenvendelseRequest req2 = new WSHentHenvendelseRequest().withBehandlingsId("a");

        when(henvendelse.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(
                new WSHentHenvendelseResponse().withAny("first"),
                new WSHentHenvendelseResponse().withAny("second")
        );

        String resp1 = ((String) henvendelse.hentHenvendelse(req1).getAny());
        String resp2 = ((String) henvendelse.hentHenvendelse(req2).getAny());

        assertThat(resp1, is(resp2));
    }
}
