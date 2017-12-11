package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon;


import net.sf.ehcache.Cache;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class})
public class OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest extends CacheTest {

    public static final String CACHE_NAME = "organisasjonEnhetKontaktinformasjonCache";
    public static final String ENHET_ID = "1337";
    public static final String ENHET_ID_OPPSLAG_2 = "1500";
    public static final String ENHETSNAVN = "NAV Molde";
    public static final String ENHETSNAVN_OPPSLAG_2 = "NAV Trondheim";

    @Inject
    private OrganisasjonEnhetKontaktinformasjonV1 organisasjonEnhetKontaktinformasjonV1;

    public OrganisasjonEnhetKontaktinformasjonV1EndpointCacheTest() {
        super(CACHE_NAME);
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
    }

    @Test
    public void gjorIkkeSammeOppslagToGangerPaRad() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));

        Cache organisasjonEnhetKontantinformasjonCache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertEquals(1, organisasjonEnhetKontantinformasjonCache.getSize());
        assertEquals(1, organisasjonEnhetKontantinformasjonCache.getLiveCacheStatistics().getInMemoryHitCount());
        assertEquals(1, organisasjonEnhetKontantinformasjonCache.getLiveCacheStatistics().getCacheMissCount());
    }

    @Test
    public void gjorNyttOppslagHvisAnnenEnhetId() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID));
        organisasjonEnhetKontaktinformasjonV1.hentKontaktinformasjonForEnhetBolk(lagRequest(ENHET_ID_OPPSLAG_2));

        Cache cache = getCache().getCacheManager().getCache(CACHE_NAME);
        assertEquals(2, cache.getSize());
        assertEquals(0, cache.getLiveCacheStatistics().getInMemoryHitCount());
        assertEquals(2, cache.getLiveCacheStatistics().getCacheMissCount());
    }

    private HentKontaktinformasjonForEnhetBolkRequest lagRequest(String enhetId) {
        HentKontaktinformasjonForEnhetBolkRequest request = new HentKontaktinformasjonForEnhetBolkRequest();
        request.getEnhetIdListe().add(enhetId);
        return request;
    }

}