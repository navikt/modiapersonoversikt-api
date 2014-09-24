package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KodeverkV2EndpointConfig.class,
        KodeverkWrapperTestConfig.class
})
public class KodeverkCacheTest extends CacheTest {

    public static final String CACHE_NAME = "kodeverkCache";

    @Inject
    private KodeverkPortType kodeverk;

    public KodeverkCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    public void cacheManager_harEntryForKodeverk_etterKallTilKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        XMLHentKodeverkRequest request1 = new XMLHentKodeverkRequest().withNavn("navn");
        XMLHentKodeverkRequest request2 = new XMLHentKodeverkRequest().withNavn("navn");

        String resp1 = kodeverk.hentKodeverk(request1).getKodeverk().getNavn();
        String resp2 = kodeverk.hentKodeverk(request2).getKodeverk().getNavn();

        assertThat(resp1, is(resp2));
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}
