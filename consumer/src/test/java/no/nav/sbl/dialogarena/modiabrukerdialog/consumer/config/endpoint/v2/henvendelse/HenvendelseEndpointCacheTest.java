package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HenvendelseEndpointCacheTestConfig.class})
public class HenvendelseEndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private HenvendelsePortType henvendelse;

    public HenvendelseEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeAll
    public static void setup() {
        setupKeyAndTrustStore();
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
