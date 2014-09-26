package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KodeverkV2EndpointConfig.class,
})
public class KodeverkCacheTest extends CacheTest {

    public static final String CACHE_NAME = "kodeverkCache";

    @BeforeClass
    public static void fixEnvironment() {
        setProperty("kodeverkendpoint.v2.url", "http://www.value.com");
        setProperty(KODEVERK_KEY, "true");
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
    }

    @Inject
    private KodeverkPortType kodeverk;

    public KodeverkCacheTest() {
        super(CACHE_NAME);
    }

    @Test
    public void cacheManager_harEntryForKodeverk_etterKallTilKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        XMLHentKodeverkRequest request1 = new XMLHentKodeverkRequest().withNavn("navn1");
        XMLHentKodeverkRequest request2 = new XMLHentKodeverkRequest().withNavn("navn2");

        kodeverk = mock(KodeverkPortType.class);
        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request1);
        kodeverk.hentKodeverk(request2);
        kodeverk.hentKodeverk(request2);

        int antallCacheinstanser = getCache().getSize();

        assertThat(antallCacheinstanser, is(2));
    }

}
