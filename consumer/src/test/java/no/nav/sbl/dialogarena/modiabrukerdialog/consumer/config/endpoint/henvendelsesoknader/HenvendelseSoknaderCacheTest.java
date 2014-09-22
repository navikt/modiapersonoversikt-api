package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader;

import net.sf.ehcache.Ehcache;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.SubjectHandlerTestConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheConfiguration.class,
        SubjectHandlerTestConfig.class,
        HenvendelseSoknaderEndpointConfig.class
})
public class HenvendelseSoknaderCacheTest {

    public static final String ENDPOINT_CACHE = "endpointCache";


    private static EhCacheCacheManager cm;

    @Inject
    public void setEhCacheCacheManager(EhCacheCacheManager eccm) {
        cm = eccm;
    }

    @Inject
    private HenvendelseSoknaderPortType henvendelse;

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(HENVENDELSESOKNADER_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @AfterClass
    public static void after() {
        cm.getCacheManager().shutdown();
    }

    @Before
    public void teardown() {
        getCache().removeAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void cache_virker() {
        String fodselsnummer = "string";
        when(henvendelse.hentSoknadListe(anyString())).thenReturn(
                asList(new WSSoknad().withBehandlingsId("1")),
                asList(new WSSoknad().withBehandlingsId("2")));

        String behandlingsId = henvendelse.hentSoknadListe(fodselsnummer).get(0).getBehandlingsId();
        String behandlingsId1 = henvendelse.hentSoknadListe(fodselsnummer).get(0).getBehandlingsId();

        assertThat(behandlingsId, is(behandlingsId1));
    }

    @Test
    public void cacheManager_harIkkeEntryForEndpointCache_etterKallTilPing() throws NoSuchMethodException {
        henvendelse.ping();

        assertThat(getCache().getKeys().size(), is(0));
    }

    private Ehcache getCache() {
        return ((EhCacheCache) cm.getCache(ENDPOINT_CACHE)).getNativeCache();
    }
}
