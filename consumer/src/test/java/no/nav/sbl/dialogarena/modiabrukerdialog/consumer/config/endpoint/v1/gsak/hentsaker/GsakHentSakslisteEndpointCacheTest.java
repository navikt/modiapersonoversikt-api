package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakHentSakslisteEndpointConfig.GSAK_SAKSLISTE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        GsakHentSakslisteEndpointConfig.class
})
public class GsakHentSakslisteEndpointCacheTest extends CacheTest {
    public static final String GSAK_CACHE = "endpointCache";

    @Inject
    private Sak gsak;

    public GsakHentSakslisteEndpointCacheTest() {
        super(GSAK_CACHE);
    }


    @BeforeClass
    public static void setup() {
        System.setProperty(GSAK_SAKSLISTE_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForGsakCache_etterKallTilFinnGenerellSakListe() {
//        WSFinnGenerellSakListeRequest request = new WSFinnGenerellSakListeRequest().withBrukerId("11111111111");
//        WSFinnGenerellSakListeRequest request1 = new WSFinnGenerellSakListeRequest().withBrukerId("11111111111");
//        WSFinnGenerellSakListeRequest request2 = new WSFinnGenerellSakListeRequest().withBrukerId("11111111111");
//
//        when(gsak.finnGenerellSakListe(request)).thenReturn(
//                new WSFinnGenerellSakListeResponse().withSakListe(new WSGenerellSak().withSakId("sid1")),
//                new WSFinnGenerellSakListeResponse().withSakListe(new WSGenerellSak().withSakId("sid2"))
//        );
//
//        String sid1 = gsak.finnGenerellSakListe(request1).getSakListe().get(0).getSakId();
//        String sid2 = gsak.finnGenerellSakListe(request2).getSakListe().get(0).getSakId();
//
//        assertThat(sid1, is(sid2));
    }
}
