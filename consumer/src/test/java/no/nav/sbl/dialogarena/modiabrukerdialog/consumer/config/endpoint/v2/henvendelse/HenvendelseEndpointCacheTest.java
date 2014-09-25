package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HenvendelseEndpointConfig.class
})
public class HenvendelseEndpointCacheTest extends CacheTest {
    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private HenvendelsePortType henvendelse;

    public HenvendelseEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty(HENVENDELSE_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
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
