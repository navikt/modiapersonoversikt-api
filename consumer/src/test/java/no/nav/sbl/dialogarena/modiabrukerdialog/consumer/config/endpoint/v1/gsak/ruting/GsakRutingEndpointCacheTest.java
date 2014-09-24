package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.ruting;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSBrukersok;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.ruting.GsakRutingEndpointConfig.GSAK_RUTING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        GsakRutingEndpointConfig.class
})
public class GsakRutingEndpointCacheTest extends CacheTest {
    public static final String CACHE_NAME = "endpointCache";

    @Inject
    private Ruting ruting;

    public GsakRutingEndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void setup() {
        System.setProperty(GSAK_RUTING_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForGsakCache_etterKallTilFinnAnsvarligEnhetForSak() {
        WSFinnAnsvarligEnhetForSakRequest request1 = new WSFinnAnsvarligEnhetForSakRequest()
                .withBrukersok(new WSBrukersok().withBrukerId("me").withFagomradeKode("fag"));
        WSFinnAnsvarligEnhetForSakRequest request2 = new WSFinnAnsvarligEnhetForSakRequest()
                .withBrukersok(new WSBrukersok().withBrukerId("me").withFagomradeKode("fag"));

        String resp1 = ruting.finnAnsvarligEnhetForSak(request1).getEnhetId();
        String resp2 = ruting.finnAnsvarligEnhetForSak(request2).getEnhetId();

        assertThat(resp1, is(resp2));
    }
}
